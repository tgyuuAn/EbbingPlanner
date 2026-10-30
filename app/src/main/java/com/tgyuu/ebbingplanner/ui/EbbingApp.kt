package com.tgyuu.ebbingplanner.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.util.EbbingBottomBarEnterAnimation
import com.tgyuu.common.util.addFocusCleaner
import com.tgyuu.designsystem.component.EbbingSnackBar
import com.tgyuu.designsystem.component.EbbingSnackBarHost
import com.tgyuu.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.navigation.AppBottomBar
import com.tgyuu.ebbingplanner.navigation.AppNavHost
import com.tgyuu.ebbingplanner.navigation.AppNavigationRail
import com.tgyuu.ebbingplanner.navigation.AppTabletBottomBar
import com.tgyuu.ebbingplanner.navigation.TopLevelDestination
import com.tgyuu.sync.network.NetworkBanner
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Composable
internal fun EbbingApp(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    EbbingModalBottomSheet(
        sheetState = appState.bottomSheetState,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val windowWidthSize = currentWindowAdaptiveInfo()
                .windowSizeClass
                .windowWidthSizeClass

            when (windowWidthSize) {
                WindowWidthSizeClass.COMPACT -> PhoneContent(
                    appState = appState,
                    snackBarHostState = snackBarHostState,
                    sheetState = appState.bottomSheetState.state,
                )

                else -> TabletContent(
                    appState = appState,
                    snackBarHostState = snackBarHostState,
                    sheetState = appState.bottomSheetState.state,
                )
            }

            NetworkBanner(
                needsNetwork = appState.requireNetworkConnection,
                networkState = appState.networkState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun PhoneContent(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
    sheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
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
            EbbingBottomBarEnterAnimation(
                visible = !appState.shouldHideBottomBar,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    navigateToBottomBarDestination = { appState.navigate(it) },
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        AppNavHost(
            appState = appState,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .addFocusCleaner(focusManager),
        )

        HandleDoubleBackToExit(
            appState = appState,
            snackBarHostState = snackBarHostState,
        )

        // sheet BackHandler는 exit보다 뒤에 등록 → 우선순위 높음
        BackHandler(enabled = sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        }
    }
}

@Composable
private fun TabletContent(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
    sheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val showNavigation = !appState.shouldHideTabletNavigation

    // 태블릿은 하위 화면에서도 내비게이션이 보이므로, 진입한 탭을 기억해 선택 상태로 유지해요
    val currentTopLevel = TopLevelDestination.topLevelDestinations.firstOrNull {
        currentDestination.isRouteInHierarchy(it.route)
    }
    var selectedTopLevel by rememberSaveable { mutableStateOf(TopLevelDestination.HOME) }
    LaunchedEffect(currentTopLevel) {
        if (currentTopLevel != null) selectedTopLevel = currentTopLevel
    }

    Scaffold(
        containerColor = EbbingTheme.colors.background,
        snackbarHost = {
            EbbingSnackBarHost(
                hostState = snackBarHostState,
                snackbar = { EbbingSnackBar(it) },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        BoxWithConstraints(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            // 가로 모드는 왼쪽 레일, 세로 모드는 하단 탭바
            val isLandscape = maxWidth > maxHeight

            val content: @Composable (Modifier) -> Unit = { contentModifier ->
                Box(contentModifier) {
                    AppNavHost(
                        appState = appState,
                        modifier = Modifier
                            .addFocusCleaner(focusManager)
                            .padding(vertical = 20.dp),
                    )

                    HandleDoubleBackToExit(
                        appState = appState,
                        snackBarHostState = snackBarHostState,
                    )

                    BackHandler(enabled = sheetState.isVisible) {
                        scope.launch { sheetState.hide() }
                    }
                }
            }

            if (isLandscape) {
                Row(Modifier.fillMaxSize()) {
                    if (showNavigation) {
                        AppNavigationRail(
                            selectedDestination = selectedTopLevel,
                            navigateToDestination = { appState.navigateToTopLevelDestination(it) },
                        )
                    }
                    content(Modifier.weight(1f))
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    content(Modifier.weight(1f))
                    if (showNavigation) {
                        AppTabletBottomBar(
                            selectedDestination = selectedTopLevel,
                            navigateToDestination = { appState.navigateToTopLevelDestination(it) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleDoubleBackToExit(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exitMessage = stringResource(com.tgyuu.designsystem.R.string.exit_confirm_snackbar)
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = appState.isRootRoute) {
        val activity = context as? androidx.activity.ComponentActivity
        if (System.currentTimeMillis() - backPressedTime <= 2000L) {
            activity?.finish()
        } else {
            scope.launch {
                snackBarHostState.showSnackbar(exitMessage)
            }
            backPressedTime = System.currentTimeMillis()
        }
    }
}

internal fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false

