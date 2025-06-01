package com.tgyuu.ebbingplanner.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.EbbingBottomBarAnimation
import com.tgyuu.common.ui.addFocusCleaner
import com.tgyuu.common.ui.repeatOnStarted
import com.tgyuu.designsystem.component.EbbingModalBottomSheet
import com.tgyuu.designsystem.component.EbbingSnackBar
import com.tgyuu.designsystem.component.EbbingSnackBarHost
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.ui.navigation.AppBottomBar
import com.tgyuu.ebbingplanner.ui.navigation.AppNavHost
import com.tgyuu.ebbingplanner.ui.navigation.TopLevelDestination
import com.tgyuu.ebbingplanner.ui.update.UpdateDialog
import com.tgyuu.ebbingplanner.ui.widget.calendar.CalendarWidgetReceiver
import com.tgyuu.ebbingplanner.ui.widget.todaytodo.TodayTodoWidgetReceiver
import com.tgyuu.ebbingplanner.ui.widget.util.RefreshAction
import com.tgyuu.navigation.HomeBaseRoute
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.NavigationEvent.BottomBarTo
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.navigation.isRootRoute
import com.tgyuu.navigation.isRouteInHierarchy
import com.tgyuu.navigation.shouldHideBottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationBus: NavigationBus

    @Inject
    lateinit var eventBus: EventBus

    private val viewModel: MainViewModel by viewModels()
    private var isInitialized: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isInitialized }

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        lifecycleScope.launch {
            launch { viewModel.getUpdateInfo() }
            val insertDefaultTagJob = launch { viewModel.insertDefaultTag() }
            val checkOnboardingJob = launch { viewModel.isFirstAppOpen() }
            insertDefaultTagJob.join()
            checkOnboardingJob.join()
            handleDestinationIntent(intent)
            isInitialized = false
        }

        setContent {
            EbbingTheme {
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                var bottomSheetContent by remember { mutableStateOf<BottomSheetContent?>(null) }
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    skipHalfExpanded = true,
                )

                val updateInfo by viewModel.updateInfo.collectAsStateWithLifecycle()
                UpdateDialog(updateInfo)

                CollectEventFlows(
                    navController = navController,
                    sheetState = sheetState,
                    snackBarHostState = snackBarHostState,
                    setBottomSheetContent = { bottomSheetContent = it },
                )

                val windowSize = currentWindowAdaptiveInfo().windowSizeClass
                val currentDestination = navController.currentBackStackEntryAsState()
                    .value?.destination

                EbbingModalBottomSheet(
                    sheetState = sheetState,
                    sheetContent = bottomSheetContent,
                ) {
                    if (windowSize.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                        PhoneContent(
                            navController = navController,
                            snackBarHostState = snackBarHostState,
                            sheetState = sheetState,
                            currentDestination = currentDestination,
                        )
                    } else {
                        TabletContent(
                            navController = navController,
                            snackBarHostState = snackBarHostState,
                            currentDestination = currentDestination,
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        sendBroadcast(
            Intent(this, TodayTodoWidgetReceiver::class.java).apply {
                action = RefreshAction.TODAY_TODO_UPDATE_ACTION
            }
        )

        sendBroadcast(
            Intent(this, CalendarWidgetReceiver::class.java).apply {
                action = RefreshAction.CALENDAR_UPDATE_ACTION
            }
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDestinationIntent(intent)
    }

    private fun handleDestinationIntent(intent: Intent) {
        lifecycleScope.launch {
            intent.extras?.getString(KEY_DESTINATION)?.let { destination ->
                when (destination) {
                    ADD_TODO -> navigationBus.navigate(
                        NavigationEvent.To(
                            HomeGraph.AddTodoRoute(LocalDate.now().toFormattedString())
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun PhoneContent(
        navController: NavHostController,
        snackBarHostState: SnackbarHostState,
        sheetState: ModalBottomSheetState,
        currentDestination: NavDestination?,
    ) {
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        Scaffold(
            containerColor = EbbingTheme.colors.background,
            snackbarHost = {
                EbbingSnackBarHost(
                    hostState = snackBarHostState,
                    snackbar = { EbbingSnackBar(it) },
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
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .addFocusCleaner(focusManager),
            )

            BackHandler(enabled = sheetState.isVisible) {
                scope.launch { sheetState.hide() }
            }

            HandleDoubleBackToExit(currentDestination)
        }
    }

    @Composable
    fun TabletContent(
        navController: NavHostController,
        snackBarHostState: SnackbarHostState,
        currentDestination: NavDestination?,
    ) {
        val focusManager = LocalFocusManager.current
        val showDrawer = currentDestination?.shouldHideBottomBar() == false
        val transition = updateTransition(targetState = showDrawer, label = "drawerAnim")

        val drawerWidth by transition.animateDp(
            label = "width", transitionSpec = { tween(250) }
        ) { shown -> if (shown) 200.dp else 0.dp }

        val drawerAlpha by transition.animateFloat(
            label = "alpha", transitionSpec = { tween(250) }
        ) { shown -> if (shown) 1f else 0f }

        Scaffold(
            containerColor = EbbingTheme.colors.background,
            snackbarHost = {
                EbbingSnackBarHost(
                    hostState = snackBarHostState,
                    snackbar = { EbbingSnackBar(it) },
                )
            }
        ) { innerPadding ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .animateContentSize()
            ) {
                Box(
                    Modifier
                        .width(drawerWidth)
                        .padding(top = 20.dp)
                        .graphicsLayer { alpha = drawerAlpha }
                ) {
                    val navigationItemColor = NavigationSuiteDefaults.itemColors(
                        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
                            selectedIconColor = EbbingTheme.colors.white,
                            unselectedIconColor = EbbingTheme.colors.dark3,
                            selectedTextColor = EbbingTheme.colors.white,
                            unselectedTextColor = EbbingTheme.colors.dark3,
                            selectedContainerColor = EbbingTheme.colors.primaryDefault,
                        )
                    )

                    NavigationSuiteScaffold(
                        layoutType = NavigationSuiteType.NavigationDrawer,
                        navigationSuiteColors = NavigationSuiteDefaults.colors(
                            navigationDrawerContainerColor = EbbingTheme.colors.background,
                            navigationDrawerContentColor = EbbingTheme.colors.white,
                        ),
                        navigationSuiteItems = {
                            TopLevelDestination.topLevelDestinations.forEach { dest ->
                                item(
                                    selected = currentDestination.isRouteInHierarchy(dest.route),
                                    icon = {
                                        Icon(
                                            painterResource(dest.iconDrawableId),
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.title,
                                            style = EbbingTheme.typography.headingSM,
                                            textAlign = TextAlign.Center,
                                        )
                                    },
                                    colors = navigationItemColor,
                                    onClick = {
                                        when (dest) {
                                            TopLevelDestination.HOME ->
                                                navController.navigate(HomeGraph.HomeRoute())

                                            TopLevelDestination.SETTING ->
                                                navController.navigate(SettingGraph.SettingRoute)
                                        }
                                    }
                                )
                            }
                        }
                    )
                }

                Box(Modifier.weight(1f)) {
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier
                            .addFocusCleaner(focusManager)
                            .padding(vertical = 20.dp),
                    )

                    HandleDoubleBackToExit(currentDestination)
                }
            }
        }
    }

    @Composable
    private fun CollectEventFlows(
        navController: NavHostController,
        sheetState: ModalBottomSheetState,
        snackBarHostState: SnackbarHostState,
        setBottomSheetContent: (BottomSheetContent) -> Unit,
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
                                setBottomSheetContent(event.content)
                                focusManager.clearFocus()
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
    }

    @Composable
    fun HandleDoubleBackToExit(currentDestination: NavDestination?) {
        val scope = rememberCoroutineScope()
        var backPressedTime by remember { mutableLongStateOf(0L) }

        BackHandler(enabled = currentDestination.isRootRoute()) {
            if (System.currentTimeMillis() - backPressedTime <= 2000L) {
                finish()
            } else {
                scope.launch {
                    eventBus.sendEvent(
                        EbbingEvent.ShowSnackBar("뒤로 가기를 한 번 더 누르면 앱이 종료돼요")
                    )
                }
            }
            backPressedTime = System.currentTimeMillis()
        }
    }

    companion object {
        const val KEY_DESTINATION = "destination"
        const val ADD_TODO = "addTodo"
    }
}
