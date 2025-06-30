package com.tgyuu.ebbingplanner

import android.content.Intent
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.EbbingBottomBarAnimation
import com.tgyuu.common.ui.addFocusCleaner
import com.tgyuu.designsystem.component.EbbingSnackBar
import com.tgyuu.designsystem.component.EbbingSnackBarHost
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetState
import com.tgyuu.designsystem.component.bottomsheet.EbbingModalBottomSheet
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.ebbingplanner.ui.EbbingAppState
import com.tgyuu.ebbingplanner.ui.navigation.AppBottomBar
import com.tgyuu.ebbingplanner.ui.navigation.AppNavHost
import com.tgyuu.ebbingplanner.ui.navigation.TopLevelDestination
import com.tgyuu.ebbingplanner.ui.update.UpdateDialog
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.SettingGraph
import com.tgyuu.sync.network.NetworkBanner
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Composable
internal fun EbbingApp(
    appState: EbbingAppState,
    bottomSheetState: EbbingBottomSheetState,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    EbbingModalBottomSheet(
        sheetState = bottomSheetState,
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val windowSize = currentWindowAdaptiveInfo().windowSizeClass
            when (windowSize.windowWidthSizeClass) {
                WindowWidthSizeClass.COMPACT -> PhoneContent(
                    appState = appState,
                    snackBarHostState = snackBarHostState,
                    sheetState = bottomSheetState.state,
                )

                else -> TabletContent(
                    appState = appState,
                    snackBarHostState = snackBarHostState,
                    sheetState = bottomSheetState.state,
                )
            }

            NetworkBanner(
                needsNetwork = appState.requireNetworkConnection,
                networkState = appState.networkState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }

    var isDialogVisible by remember(appState.shouldUpdate) { mutableStateOf(appState.shouldUpdate) }
    UpdateDialogHost(
        shouldShow = isDialogVisible,
        updateInfo = appState.updateInfo,
        onDismissRequest = { isDialogVisible = false }
    )
}


@Composable
fun PhoneContent(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
    sheetState: ModalBottomSheetState,
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
            EbbingBottomBarAnimation(
                visible = !appState.shouldHideBottomBar,
                modifier = Modifier.navigationBarsPadding(),
            ) {
                AppBottomBar(
                    currentDestination = currentDestination,
                    navigateToBottomBarDestination = { appState.navigate(it) },
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            appState = appState,
            modifier = Modifier
                .padding(innerPadding)
                .addFocusCleaner(focusManager),
        )

        BackHandler(enabled = sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        }

        HandleDoubleBackToExit(
            appState = appState,
            snackBarHostState = snackBarHostState,
        )
    }
}

@Composable
fun TabletContent(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
    sheetState: ModalBottomSheetState,
) {
    val currentDestination = appState.currentDestination
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val transition = updateTransition(
        targetState = !appState.shouldHideBottomBar,
        label = "drawerAnim",
    )

    val drawerWidth by transition.animateDp(
        label = "width",
        transitionSpec = { tween(250) },
    ) { shown -> if (shown) 200.dp else 0.dp }

    val drawerAlpha by transition.animateFloat(
        label = "alpha",
        transitionSpec = { tween(250) },
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
                    .padding(start = 12.dp, top = 20.dp)
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
                            val selected = currentDestination.isRouteInHierarchy(dest.route)

                            item(
                                selected = selected,
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
                                            appState.navigate(HomeGraph.HomeRoute())

                                        TopLevelDestination.SETTING ->
                                            appState.navigate(SettingGraph.SettingRoute)
                                    }
                                }
                            )
                        }
                    }
                )
            }

            Box(Modifier.weight(1f)) {
                AppNavHost(
                    appState = appState,
                    modifier = Modifier
                        .addFocusCleaner(focusManager)
                        .padding(vertical = 20.dp),
                )

                BackHandler(enabled = sheetState.isVisible) {
                    scope.launch { sheetState.hide() }
                }

                HandleDoubleBackToExit(
                    appState = appState,
                    snackBarHostState = snackBarHostState,
                )
            }
        }
    }
}

@Composable
private fun UpdateDialogHost(
    shouldShow: Boolean,
    updateInfo: UpdateInfo?,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current

    if (shouldShow && updateInfo != null) {
        UpdateDialog(
            updateInfo = updateInfo,
            onDismissRequest = onDismissRequest,
            onUpdateClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=com.tgyuu.ebbingplanner".toUri(),
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            },
        )
    }
}

@Composable
private fun HandleDoubleBackToExit(
    appState: EbbingAppState,
    snackBarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var backPressedTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = appState.isRootRoute) {
        val activity = context as? androidx.activity.ComponentActivity
        if (System.currentTimeMillis() - backPressedTime <= 2000L) {
            activity?.finish()
        } else {
            scope.launch {
                snackBarHostState.showSnackbar("뒤로 가기를 한 번 더 누르면 앱이 종료돼요")
            }
            backPressedTime = System.currentTimeMillis()
        }
    }
}

internal fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false

