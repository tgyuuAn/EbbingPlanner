package com.tgyuu.ebbingplanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.analytics.LocalAnalyticsHelper
import com.tgyuu.analytics.TrackNavigationDestination
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.systemcallback.LocalAnimationsEnabled
import com.tgyuu.common.systemcallback.MemoryAnimationController
import com.tgyuu.common.toFormattedString
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.ebbingplanner.systemcallback.SystemCallbacksRegistrar
import com.tgyuu.ebbingplanner.ui.EbbingApp
import com.tgyuu.ebbingplanner.ui.SoftUpdateDialog
import com.tgyuu.ebbingplanner.ui.rememberEbbingAppState
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.NavigationEvent.BottomBarTo
import com.tgyuu.sync.network.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
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
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var systemCallbacksRegistrar: SystemCallbacksRegistrar

    private var isInitialized = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isInitialized }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 메모리 Component Callback 등록
        registerComponentCallbacks(systemCallbacksRegistrar)
        handleWidgetIntent(intent)
        lifecycleScope.launch {
            viewModel.initAppState()
            isInitialized = false
        }

        setContent {
            val theme by viewModel.theme.collectAsStateWithLifecycle()
            val updateInfo by viewModel.updateInfo.collectAsStateWithLifecycle()
            var isDialogVisible by remember(updateInfo) {
                mutableStateOf(shouldShowUpdateDialog(updateInfo))
            }

            val navController = rememberNavController()
            val bottomSheetState = rememberEbbingBottomSheetState()
            val snackBarHostState = remember { SnackbarHostState() }
            val appState = rememberEbbingAppState(
                navController = navController,
                networkMonitor = networkMonitor,
                bottomSheetState = bottomSheetState,
            )

            HandleSideEffects(
                navController = navController,
                bottomSheetState = bottomSheetState,
                snackBarHostState = snackBarHostState,
            )

            EbbingTheme(theme = theme) {
                CompositionLocalProvider(
                    LocalAnalyticsHelper provides analyticsHelper,
                    LocalAnimationsEnabled provides MemoryAnimationController.animationsEnabled,
                ) {
                    EbbingApp(
                        appState = appState,
                        snackBarHostState = snackBarHostState,
                    )

                    SoftUpdateDialog(
                        shouldShow = isDialogVisible,
                        updateInfo = updateInfo,
                        onDismissRequest = { isDialogVisible = false }
                    )

                    TrackNavigationDestination(navController)
                }
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

    override fun onDestroy() {
        super.onDestroy()
        unregisterComponentCallbacks(systemCallbacksRegistrar)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetIntent(intent)
    }

    private fun handleWidgetIntent(intent: Intent) {
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

    private fun shouldShowUpdateDialog(info: UpdateInfo?): Boolean {
        if (info == null) return false

        val currentVersion = this.packageManager.getPackageInfo(this.packageName, 0)
            .versionName ?: return false

        return checkShouldUpdate(currentVersion, info.minVersion)
    }

    private fun checkShouldUpdate(currentVersion: String, minVersion: String): Boolean {
        val current = normalizeVersion(currentVersion)
        val min = normalizeVersion(minVersion)
        return current.zip(min).any { (cur, min) -> cur < min }
    }

    private fun normalizeVersion(version: String): List<Int> = version.split('.')
        .map { it.toIntOrNull() ?: 0 }
        .let { if (it.size == 2) it + 0 else it }

    @Composable
    private fun HandleSideEffects(
        navController: NavHostController,
        bottomSheetState: EbbingBottomSheetState,
        snackBarHostState: SnackbarHostState,
    ) {
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        LaunchedEffect(navController, bottomSheetState, snackBarHostState) {
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

    companion object {
        const val KEY_DESTINATION = "destination"
        const val KEY_SELECTED_DATE = "selectedDate"
        const val ADD_TODO = "addTodo"
    }
}
