package com.tgyuu.ebbingplanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.now
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.util.LocalAnimationsEnabled
import com.tgyuu.common.util.MemoryAnimationController
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.designsystem.component.bottomsheet.rememberEbbingBottomSheetState
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.backup.AutoBackupManager
import com.tgyuu.ebbingplanner.ui.EbbingApp
import com.tgyuu.ebbingplanner.ui.HandleAppUpdate
import com.tgyuu.ebbingplanner.ui.rememberEbbingAppState
import com.tgyuu.ebbingplanner.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.widget.todaytodo.TodayTodoWidgetReceiver
import com.tgyuu.ebbingplanner.widget.util.ADD_TODO
import com.tgyuu.ebbingplanner.widget.util.KEY_DESTINATION
import com.tgyuu.ebbingplanner.widget.util.KEY_SELECTED_DATE
import com.tgyuu.ebbingplanner.widget.util.RefreshAction
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.NavigationEvent.BottomBarTo
import com.tgyuu.navigation.NavigationEvent.To
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.setting.BuildConfig
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.sync.network.NetworkState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModel()

    private val navigationBus: NavigationBus by inject()
    private val eventBus: EventBus by inject()
    private val networkMonitor: NetworkMonitor by inject()
    private val analyticsHelper: AnalyticsHelper by inject()
    private val autoBackupManager: AutoBackupManager by inject()

    private var isInitialized = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isInitialized }
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        handleWidgetIntent(intent)
        lifecycleScope.launch {
            viewModel.initAppState()
            isInitialized = false
        }

        lifecycleScope.launch {
            networkMonitor.networkState.collect { state ->
                if (state == NetworkState.Connected) {
                    autoBackupManager.tryPendingBackup()
                }
            }
        }

        setContent {
            val updateState by viewModel.updateState.collectAsStateWithLifecycle()
            val theme by viewModel.theme.collectAsStateWithLifecycle()

            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            val bottomSheetState = rememberEbbingBottomSheetState()
            val snackBarHostState = remember { SnackbarHostState() }

            EbbingTheme(theme = theme) {
                CompositionLocalProvider(
                    LocalAnalyticsHelper provides analyticsHelper,
                    LocalAnimationsEnabled provides MemoryAnimationController.animationsEnabled,
                ) {
                    val appState = rememberEbbingAppState(
                        navController = navController,
                        networkMonitor = networkMonitor,
                        bottomSheetState = bottomSheetState,
                    )

                    EbbingApp(
                        appState = appState,
                        snackBarHostState = snackBarHostState,
                    )

                    if (!appState.isWebViewRoute) {
                        HandleAppUpdate(
                            updateState = updateState,
                            onClickUpdateInfo = {
                                scope.launch {
                                    navigationBus.navigate(
                                        To(
                                            SettingGraph.WebViewRoute(
                                                title = getString(com.tgyuu.designsystem.R.string.setting_announcement),
                                                url = BuildConfig.EBBING_NOTICE_URL,
                                            )
                                        )
                                    )
                                }
                            },
                        )
                    }

                    HandleSideEffects(
                        navController = navController,
                        bottomSheetState = bottomSheetState,
                        snackBarHostState = snackBarHostState,
                    )
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            lifecycleScope.launch { autoBackupManager.onAppStop() }
        }

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

    @Composable
    private fun HandleSideEffects(
        navController: NavHostController,
        bottomSheetState: EbbingBottomSheetState,
        snackBarHostState: SnackbarHostState,
    ) {
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        LaunchedEffect(navController, bottomSheetState, snackBarHostState) {
            // NavHost의 graph가 설정될 때까지 대기 (위젯에서 앱 실행 시 race condition 방지)
            navController.currentBackStackEntryFlow.first()

            launch {
                navigationBus.navigationFlow.collect { event ->
                    snackBarHostState.currentSnackbarData?.dismiss()

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
                            if (bottomSheetState.state.isVisible) {
                                bottomSheetState.hide()
                            }
                            bottomSheetState.setBottomSheetContent(event.content)
                            focusManager.clearFocus()
                            bottomSheetState.show()
                        }

                        EbbingEvent.HideBottomSheet -> scope.launch {
                            bottomSheetState.hide()
                            eventBus.notifyBottomSheetHidden()
                        }

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
