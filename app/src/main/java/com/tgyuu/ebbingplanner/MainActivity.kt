package com.tgyuu.ebbingplanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.repeatOnStarted
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.ebbingplanner.ui.rememberEbbingAppState
import com.tgyuu.ebbingplanner.ui.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.ui.widget.todaytodo.TodayTodoWidgetReceiver
import com.tgyuu.ebbingplanner.ui.widget.util.RefreshAction
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.NavigationEvent.BottomBarTo
import com.tgyuu.sync.network.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var navigationBus: NavigationBus

    @Inject
    lateinit var eventBus: EventBus

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var configRepository: ConfigRepository
    private var isInitialized: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isInitialized }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        lifecycleScope.launch {
            initAppState()
            handleDestinationIntent(intent)
            isInitialized = false
        }

        setContent {
            val navController = rememberNavController()
            val bottomSheetState = rememberEbbingBottomSheetState()
            val snackBarHostState = remember { SnackbarHostState() }
            val appState = rememberEbbingAppState(
                navController = navController,
                networkMonitor = networkMonitor,
                configRepository = configRepository,
            )

            HandleSideEffects(
                navController = navController,
                bottomSheetState = bottomSheetState,
                snackBarHostState = snackBarHostState,
            )

            EbbingTheme {
                EbbingApp(
                    appState = appState,
                    bottomSheetState = bottomSheetState,
                    snackBarHostState = snackBarHostState,
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sendBroadcast(
            Intent(this, TodayTodoWidgetReceiver::class.java).apply {
                action = RefreshAction.UPDATE_ACTION
            }
        )

        sendBroadcast(
            Intent(this, CalendarWidgetReceiver::class.java).apply {
                action = RefreshAction.UPDATE_ACTION
            }
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDestinationIntent(intent)
    }

    private suspend fun initAppState() = coroutineScope {
        val getUpdateInfoJob = launch { viewModel.getUpdateInfo() }
        val insertDefaultTagJob = launch { viewModel.insertDefaultTag() }
        val checkOnboardingJob = launch { viewModel.isFirstAppOpen() }
        val ensureUUIDExistsJob = launch { viewModel.ensureUUIDExists() }

        getUpdateInfoJob.join()
        insertDefaultTagJob.join()
        checkOnboardingJob.join()
        ensureUUIDExistsJob.join()

        // UUID가 없을경우, 생성 이후 호출해야 하므로 동기적으로 호출
        viewModel.setUserId()
    }

    private fun handleDestinationIntent(intent: Intent) {
        lifecycleScope.launch {
            intent.extras?.getString(KEY_DESTINATION)?.let { destination ->
                when (destination) {
                    ADD_TODO -> {
                        val selectedDate = intent.extras?.getString(KEY_SELECTED_DATE)
                            ?.let { LocalDate.parse(it) } ?: LocalDate.now()

                        navigationBus.navigate(
                            NavigationEvent.To(
                                HomeGraph.AddTodoRoute(selectedDate.toFormattedString())
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HandleSideEffects(
        navController: NavHostController,
        bottomSheetState: EbbingBottomSheetState,
        snackBarHostState: SnackbarHostState,
    ) {
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            repeatOnStarted {
                launch {
                    navigationBus.navigationFlow.collect { event ->
                        eventBus.sendEvent(EbbingEvent.HideSnackBar)

                        when (event) {
                            is NavigationEvent.To -> {
                                val navOptions = navOptions {
                                    if (event.popUpTo) {
                                        popUpTo(
                                            navController.currentBackStackEntry?.destination?.route
                                                ?: navController.graph.startDestinationRoute
                                                ?: HomeBaseRoute.toString()
                                        ) { inclusive = true }
                                    }
                                    launchSingleTop = true
                                }

                                navController.navigate(
                                    route = event.route,
                                    navOptions = navOptions
                                )
                            }

                            is NavigationEvent.Up -> navController.navigateUp()

                            is NavigationEvent.TopLevelTo -> {
                                val topLevelNavOptions = navOptions {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }

                                navController.navigate(
                                    route = event.route,
                                    navOptions = topLevelNavOptions
                                )
                            }

                            is BottomBarTo -> {
                                val topLevelNavOptions = navOptions {
                                    popUpTo(HomeGraph.HomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }

                                navController.navigate(
                                    route = event.route,
                                    navOptions = topLevelNavOptions
                                )
                            }
                        }
                    }
                }

                launch {
                    eventBus.eventFlow.collect { event ->
                        when (event) {
                            is EbbingEvent.ShowBottomSheet -> scope.launch {
                                bottomSheetState.setBottomSheetContent(event.content)
                                focusManager.clearFocus()
                                bottomSheetState.show()
                            }

                            EbbingEvent.HideBottomSheet -> scope.launch { bottomSheetState.hide() }
                            is EbbingEvent.ShowSnackBar -> scope.launch {
                                snackBarHostState.currentSnackbarData?.dismiss()
                                snackBarHostState.showSnackbar(event.msg)
                            }

                            EbbingEvent.HideSnackBar -> snackBarHostState.currentSnackbarData?.dismiss()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_DESTINATION = "destination"
        const val KEY_SELECTED_DATE = "selectedDate"
        const val ADD_TODO = "addTodo"
    }
}
