package com.tgyuu.ebbingplanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.EbbingBottomBarAnimation
import com.tgyuu.common.ui.addFocusCleaner
import com.tgyuu.common.ui.repeatOnStarted
import com.tgyuu.designsystem.component.EbbingModalBottomSheet
import com.tgyuu.designsystem.component.EbbingSnackBar
import com.tgyuu.designsystem.component.EbbingSnackBarHost
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.ui.navigation.AppBottomBar
import com.tgyuu.ebbingplanner.ui.navigation.AppNavHost
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.NavigationEvent.BottomBarTo
import com.tgyuu.navigation.shouldHideBottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var navigationBus: NavigationBus

    @Inject
    lateinit var eventBus: EventBus

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.insertDefaultTag()

        setContent {
            EbbingTheme {
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                var bottomSheetContent by remember { mutableStateOf<BottomSheetContent?>(null) }
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    skipHalfExpanded = true,
                )

                LaunchedEffect(Unit) {
                    repeatOnStarted {
                        launch {
                            navigationBus.navigationFlow.collect { event ->
                                handleNavigationEvent(
                                    navController = navController,
                                    event = event,
                                )
                            }
                        }

                        launch {
                            eventBus.eventFlow.collect { event ->
                                when (event) {
                                    is EbbingEvent.ShowBottomSheet -> scope.launch {
                                        bottomSheetContent = event.content
                                        sheetState.show()
                                    }

                                    EbbingEvent.HideBottomSheet -> scope.launch { sheetState.hide() }
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

                val currentDestination = navController.currentBackStackEntryAsState()
                    .value?.destination

                EbbingModalBottomSheet(
                    sheetState = sheetState,
                    sheetContent = bottomSheetContent,
                ) {
                    val focusManager = LocalFocusManager.current

                    Scaffold(
                        containerColor = EbbingTheme.colors.background,
                        snackbarHost = {
                            EbbingSnackBarHost(
                                hostState = snackBarHostState,
                                snackbar = { snackBarData -> EbbingSnackBar(snackBarData) },
                            )
                        },
                        bottomBar = {
                            EbbingBottomBarAnimation(
                                visible = currentDestination?.shouldHideBottomBar() == false,
                                modifier = Modifier.navigationBarsPadding(),
                            ) {
                                AppBottomBar(
                                    currentDestination = currentDestination,
                                    navigateToBottomBarDestination = { navController.navigate(it) },
                                )
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier
                                .padding(innerPadding)
                                .addFocusCleaner(focusManager),
                        )
                    }
                }
            }
        }
    }

    private fun handleNavigationEvent(
        navController: NavController,
        event: NavigationEvent
    ) {
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
