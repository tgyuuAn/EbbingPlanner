package com.tgyuu.shared.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.WindowInsets
import com.tgyuu.shared.ui.feature.home.HomeIntent
import com.tgyuu.shared.navigation.WidgetDeepLink
import com.tgyuu.shared.navigation.AppExternalEvents
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import com.tgyuu.shared.ui.feature.setting.SettingMenu
import com.tgyuu.shared.navigation.RootTab
import com.tgyuu.shared.designsystem.foundation.WindowLayout
import com.tgyuu.shared.designsystem.foundation.LocalWindowLayout
import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.VerticalDivider
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.ViewModelStoreOwner
import com.tgyuu.shared.designsystem.component.EbbingSnackBar
import com.tgyuu.shared.designsystem.component.EbbingSnackBarHost
import com.tgyuu.shared.navigation.LocalScreenBackHandler
import com.tgyuu.shared.navigation.OnboardingComponent
import com.tgyuu.shared.navigation.retainedViewModel
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.navigation.RootComponent
import com.tgyuu.shared.platform.AnalyticsEvent
import com.tgyuu.shared.platform.InAppReviewManager
import com.tgyuu.shared.ui.feature.home.HomeScreen
import com.tgyuu.shared.ui.feature.home.HomeViewModel
import com.tgyuu.shared.ui.feature.schedule.ScheduleScreen
import com.tgyuu.shared.ui.feature.schedule.ScheduleViewModel
import com.tgyuu.shared.ui.feature.setting.SettingScreen
import com.tgyuu.shared.ui.feature.setting.SettingViewModel
import com.tgyuu.shared.ui.feature.tag.TagScreen
import com.tgyuu.shared.ui.feature.tag.TagViewModel
import com.tgyuu.shared.ui.feature.memo.MemoScreen
import com.tgyuu.shared.ui.feature.memo.MemoViewModel
import com.tgyuu.shared.ui.feature.repeatcycle.RepeatCycleScreen
import com.tgyuu.shared.ui.feature.repeatcycle.RepeatCycleViewModel
import com.tgyuu.shared.ui.feature.sync.SyncScreen
import com.tgyuu.shared.ui.feature.sync.SyncViewModel
import com.tgyuu.shared.ui.feature.onboarding.OnboardingScreen
import com.tgyuu.shared.ui.feature.onboarding.OnboardingViewModel
import com.tgyuu.shared.ui.feature.home.addtodo.AddTodoScreen
import com.tgyuu.shared.ui.feature.home.addtodo.AddTodoViewModel
import com.tgyuu.shared.ui.feature.home.edittodo.EditTodoScreen
import com.tgyuu.shared.ui.feature.home.edittodo.EditTodoViewModel
import com.tgyuu.shared.ui.feature.home.editdate.EditDateScreen
import com.tgyuu.shared.ui.feature.home.editdate.EditDateViewModel
import com.tgyuu.shared.ui.feature.tag.addtag.AddTagScreen
import com.tgyuu.shared.ui.feature.tag.addtag.AddTagViewModel
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.AddRepeatCycleScreen
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.AddRepeatCycleViewModel
import com.tgyuu.shared.ui.feature.tag.edittag.EditTagScreen
import com.tgyuu.shared.ui.feature.tag.edittag.EditTagViewModel
import com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle.EditRepeatCycleScreen
import com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle.EditRepeatCycleViewModel
import com.tgyuu.shared.ui.feature.setting.theme.ThemeScreen
import com.tgyuu.shared.ui.feature.setting.theme.ThemeViewModel
import com.tgyuu.shared.common.now
import com.tgyuu.shared.common.toFormattedString
import kotlinx.datetime.LocalDate
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.domain.model.ErrorBus
import com.tgyuu.shared.domain.model.UpdateState
import com.tgyuu.shared.platform.NetworkMonitor
import com.tgyuu.shared.platform.UrlOpener
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.platform.openAppSettings
import com.tgyuu.shared.ui.component.HandleAppUpdate
import com.tgyuu.shared.ui.component.NetworkBanner
import ebbingplanner.shared.generated.resources.setting_announcement
import ebbingplanner.shared.generated.resources.setting_announcement_url
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.nav_home
import ebbingplanner.shared.generated.resources.nav_schedule
import ebbingplanner.shared.generated.resources.nav_setting
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_home
import ebbingplanner.shared.generated.resources.ic_schedule
import ebbingplanner.shared.generated.resources.ic_setting

private enum class BottomNavItem(
    val iconRes: org.jetbrains.compose.resources.DrawableResource,
    val labelRes: StringResource,
) {
    HOME(Res.drawable.ic_home, Res.string.nav_home),
    SCHEDULE(Res.drawable.ic_schedule, Res.string.nav_schedule),
    SETTING(Res.drawable.ic_setting, Res.string.nav_setting),
}

@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
) {
    val childStack by component.stack.subscribeAsState()
    val activeChild = childStack.active.instance
    val todoRepository = koinInject<TodoRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val featureFlagRepository = koinInject<com.tgyuu.shared.domain.repository.FeatureFlagRepository>()
    val syncRepository = koinInject<com.tgyuu.shared.domain.repository.SyncRepository>()
    val inAppReviewManager = koinInject<InAppReviewManager>()
    val alarmRescheduler = koinInject<AlarmRescheduler>()
    val errorBus = koinInject<ErrorBus>()
    val startupAnalyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val urlOpener = koinInject<UrlOpener>()
    val networkMonitor = koinInject<NetworkMonitor>()
    val networkState by networkMonitor.networkState.collectAsState()

    // Android MainViewModel.getSoftUpdateInfo/getHardUpdateInfo: 시작 시 Remote Config 업데이트 안내를 불러와요
    var updateState by remember { mutableStateOf(UpdateState()) }
    var appStoreUrl by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        launch { runCatching { configRepository.getSoftUpdateInfo() }.onSuccess { updateState = updateState.copy(soft = it) } }
        launch { runCatching { configRepository.getHardUpdateInfo() }.onSuccess { updateState = updateState.copy(hard = it) } }
        launch { runCatching { configRepository.getAppStoreUrl() }.onSuccess { appStoreUrl = it } }
    }

    // Check if first app open → show onboarding
    LaunchedEffect(Unit) {
        // Android MainViewModel과 동일: 앱 시작 시 기본(미지정) 태그를 시드(insert onConflict=IGNORE라 멱등).
        // 시드가 없으면 콜드스타트 시 태그 바텀시트에 '미지정'이 안 나타남.
        runCatching { todoRepository.addDefaultTag() }
        if (configRepository.isFirstAppOpen()) {
            component.navigateToOnboarding()
        }
        // Android MainViewModel.initAppState 대응: UUID 보장 후 에러/분석 user id 설정
        runCatching {
            syncRepository.ensureUUIDExists()
            val uuid = syncRepository.getUuid()
            errorBus.setUserId(uuid)
            startupAnalyticsHelper.setUserId(uuid)
        }
        // iOS는 발화 시점에 내용을 정할 수 없으므로 시작 시 전체 날짜 알림을 다시 계산
        alarmRescheduler.rescheduleAll()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    // 직전 스낵바 요청 시각: 저장 직후 홈 이동처럼 '요청 → 이동'이 이어질 때 새 스낵바는 이동으로 닫지 않아요
    val lastSnackbarRequest = remember { SnackbarRequestTracker() }
    val showSnackbar: (String) -> Unit = remember(snackbarHostState) {
        { message ->
            lastSnackbarRequest.mark()
            snackbarScope.launch {
                // Android EbbingEvent.ShowSnackBar와 동일: 떠 있는 스낵바를 닫고 새로 보여줘요
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    // Android MainActivity: 화면 이동 시 떠 있는 스낵바를 닫아요
    DisposableEffect(component) {
        val cancellation = component.stack.subscribe {
            if (!lastSnackbarRequest.isFresh()) snackbarHostState.currentSnackbarData?.dismiss()
        }
        onDispose { cancellation.cancel() }
    }

    // Create ViewModels at root level so they persist across tab switches
    val homeViewModel = remember {
        HomeViewModel(
            todoRepository = todoRepository,
            configRepository = configRepository,
            onNavigateToSetting = { component.navigateToSetting() },
            onNavigateToSchedule = { component.navigateToSchedule() },
            onNavigateToAddTodo = { date ->
                component.navigateToAddTodo(date.toFormattedString())
            },
            onNavigateToEditTodo = { scheduleId ->
                component.navigateToEditTodo(scheduleId)
            },
            onNavigateToEditDate = { infoId ->
                component.navigateToEditDate(infoId)
            },
            onNavigateToMemo = { scheduleId ->
                component.navigateToMemo(scheduleId)
            },
            onNavigateToSync = { component.navigateToSync() },
            onShowSnackBar = showSnackbar,
            alarmRescheduler = alarmRescheduler,
        )
    }

    // Android HomeRoute(workedDate, showWidgetNudge) + popUpTo 대응: 홈으로 정리하고 작업한 날짜를 선택해요
    val navigateToHomeAt: (LocalDate, Boolean) -> Unit = remember(homeViewModel) {
        { workedDate, showWidgetNudge ->
            component.popToHome()
            homeViewModel.onIntent(HomeIntent.OnCurrentDateChanged(workedDate))
            homeViewModel.onIntent(HomeIntent.OnDateSelected(workedDate))
            if (showWidgetNudge) homeViewModel.showWidgetNudgeDialog()
        }
    }

    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()

    val scheduleViewModel = remember {
        ScheduleViewModel(
            todoRepository = todoRepository,
            analyticsHelper = analyticsHelper,
            onNavigateToAddTodo = { date -> component.navigateToAddTodo(date) },
            onNavigateToEditTodo = { scheduleId -> component.navigateToEditTodo(scheduleId) },
            onNavigateToEditDate = { infoId -> component.navigateToEditDate(infoId) },
            onNavigateToMemo = { scheduleId -> component.navigateToMemo(scheduleId) },
            onNavigateToEditMemo = { scheduleId -> component.navigateToEditMemo(scheduleId) },
            onShowSnackBar = showSnackbar,
            alarmRescheduler = alarmRescheduler,
        )
    }

    val settingNotificationScheduler = koinInject<com.tgyuu.shared.platform.NotificationScheduler>()
    val settingViewModel = remember {
        SettingViewModel(
            todoRepository = todoRepository,
            configRepository = configRepository,
            featureFlagRepository = featureFlagRepository,
            syncRepository = syncRepository,
            onNavigateBack = { component.navigateToHome() },
            onNavigateToTag = { component.navigateToTag() },
            onNavigateToRepeatCycle = { component.navigateToRepeatCycle() },
            onNavigateToSync = { component.navigateToSync() },
            onNavigateToRestore = { component.navigateToSyncRestore() },
            onNavigateToTheme = { component.navigateToTheme() },
            onNavigateToWidget = { component.navigateToWidget() },
            onOpenUrl = { url -> component.navigateToWebView("", url) },
            // Android SettingViewModel.navigateToWebView와 동일: 메뉴별 제목을 명시해요
            onNavigateToWebView = { title, url -> component.navigateToWebView(title, url) },
            onRequestInAppReview = { inAppReviewManager.requestReview() },
            onShowSnackbar = showSnackbar,
            analyticsHelper = analyticsHelper,
            notificationScheduler = settingNotificationScheduler,
            alarmRescheduler = alarmRescheduler,
            onOpenExternalUrl = { url -> urlOpener.openUrl(url) },
        )
    }

    // 위젯 딥링크: 해당 날짜가 선택된 홈 / 해당 날짜로 일정 등록 (Android MainActivity.handleWidgetIntent 대응)
    LaunchedEffect(Unit) {
        AppExternalEvents.deepLinks.collect { link ->
            AppExternalEvents.consumeDeepLink()
            when (link) {
                WidgetDeepLink.Open -> Unit
                is WidgetDeepLink.Home -> {
                    component.popToHome()
                    runCatching { LocalDate.parse(link.date) }.getOrNull()?.let { date ->
                        homeViewModel.onIntent(HomeIntent.OnCurrentDateChanged(date))
                        homeViewModel.onIntent(HomeIntent.OnDateSelected(date))
                    }
                }
                is WidgetDeepLink.AddTodo -> {
                    // Android TodayTodoWidgetReceiver ADD_TODO_ACTION: Click_AddTodo_{TodoWidget|CalendarWidget}
                    analyticsHelper.logClick(link.source ?: "TodoWidget", "AddTodo")
                    component.popToHome()
                    component.navigateToAddTodo(link.date)
                }
            }
        }
    }

    // 포그라운드 복귀: 위젯에서 바꾼 완료 상태 등 외부 변경을 다시 불러와요
    LaunchedEffect(Unit) {
        AppExternalEvents.foreground.collect {
            // 보고 있던 달 기준으로 다시 불러와요 (선택한 날짜는 유지)
            homeViewModel.onIntent(HomeIntent.OnCurrentDateChanged(homeViewModel.state.value.currentDate ?: LocalDate.now()))
            scheduleViewModel.loadTodoSchedules()
            // 위젯에서 완료 상태를 바꿨을 수 있으므로 알림도 다시 계산
            alarmRescheduler.rescheduleAll()
        }
    }

    // Reload data when returning to main tabs
    LaunchedEffect(activeChild) {
        if (activeChild is RootComponent.Child.Home) {
            homeViewModel.initCurrentMonthSchedules()
        }
        if (activeChild is RootComponent.Child.Schedule) {
            scheduleViewModel.loadTodoSchedules()
        }
    }

    // 화면 진입 시 screen_view 로깅 (Android 화면별 AnalyticsEvent.View 대응, 중앙 집중)
    LaunchedEffect(activeChild) {
        screenNameFor(activeChild)?.let { screenName ->
            analyticsHelper.logEvent(
                AnalyticsEvent(
                    type = AnalyticsEvent.Types.SCREEN_VIEW,
                    properties = mapOf(AnalyticsEvent.PropertiesKeys.SCREEN_NAME to screenName),
                )
            )
        }
    }

    // Check if we should show bottom navigation (only on top-level screens)
    val showBottomNav = when (activeChild) {
        is RootComponent.Child.Home,
        is RootComponent.Child.Schedule,
        is RootComponent.Child.Setting -> true
        else -> false
    }

    // Get current selected tab
    val selectedTab = when (activeChild) {
        is RootComponent.Child.Home -> BottomNavItem.HOME
        is RootComponent.Child.Schedule -> BottomNavItem.SCHEDULE
        is RootComponent.Child.Setting -> BottomNavItem.SETTING
        else -> null
    }

    val appTheme by configRepository.getAppTheme()
        .collectAsState(initial = com.tgyuu.shared.domain.model.Theme.NORMAL)

    // 태블릿은 하위 화면에서도 내비게이션을 유지하므로, 백스택에서 가장 위에 있는 탭 화면을 선택 상태로 보여줘요
    val lastTab = childStack.items.asReversed().firstNotNullOfOrNull { item ->
        when (item.instance) {
            is RootComponent.Child.Home -> TopLevelTab.HOME
            is RootComponent.Child.Schedule -> TopLevelTab.SCHEDULE
            is RootComponent.Child.Setting -> TopLevelTab.SETTING
            else -> null
        }
    } ?: TopLevelTab.HOME
    val onTabletTabSelected: (TopLevelTab) -> Unit = { tab ->
        component.navigateToTopLevelTab(
            when (tab) {
                TopLevelTab.HOME -> RootTab.HOME
                TopLevelTab.SCHEDULE -> RootTab.SCHEDULE
                TopLevelTab.SETTING -> RootTab.SETTING
            }
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val windowLayout = WindowLayout(
            isTablet = maxWidth >= LayoutConstants.TABLET_BREAKPOINT,
            isLandscape = maxWidth > maxHeight,
        )

        CompositionLocalProvider(LocalWindowLayout provides windowLayout) {
            EbbingTheme(theme = appTheme, isTablet = windowLayout.isTablet) {
                val content: @Composable (Modifier) -> Unit = { contentModifier ->
                    Surface(
                        modifier = contentModifier,
                        color = EbbingTheme.colors.background,
                    ) {
                        Children(
                            stack = component.stack,
                            // Android 태블릿 NavHost와 동일: 화면 영역 위아래 20dp 여백
                            modifier = if (windowLayout.isTablet) Modifier.padding(vertical = 20.dp) else Modifier,
                            animation = stackAnimation(fade()),
                        ) { child ->
                            val instance = child.instance
                            val owner = instance.viewModelStoreOwner
                            CompositionLocalProvider(LocalScreenBackHandler provides instance.backHandler) {
                            when (instance) {
                                is RootComponent.Child.Home -> HomeScreen(
                                    viewModel = homeViewModel,
                                    onRequestInAppReview = { inAppReviewManager.requestReview() },
                                )
                                is RootComponent.Child.Schedule -> ScheduleScreen(viewModel = scheduleViewModel)
                                is RootComponent.Child.Setting -> SettingScreen(viewModel = settingViewModel)
                                is RootComponent.Child.Tag -> TagScreenWrapper(component, owner, showSnackbar)
                                is RootComponent.Child.Memo -> MemoScreenWrapper(component, owner, instance.scheduleId, showSnackbar, navigateToHomeAt, isEditEntry = false)
                                is RootComponent.Child.RepeatCycle -> RepeatCycleScreenWrapper(component, owner, showSnackbar)
                                is RootComponent.Child.Sync -> SyncScreenWrapper(component, owner, showSnackbar)
                                is RootComponent.Child.Onboarding -> OnboardingScreenWrapper(instance.component, owner)
                                is RootComponent.Child.AddTodo -> AddTodoScreenWrapper(component, owner, instance.selectedDate, showSnackbar, navigateToHomeAt)
                                is RootComponent.Child.EditTodo -> EditTodoScreenWrapper(component, owner, instance.scheduleId, showSnackbar, navigateToHomeAt)
                                is RootComponent.Child.EditDate -> EditDateScreenWrapper(component, owner, instance.infoId, showSnackbar, navigateToHomeAt)
                                is RootComponent.Child.AddTag -> AddTagScreenWrapper(component, owner, showSnackbar)
                                is RootComponent.Child.AddRepeatCycle -> AddRepeatCycleScreenWrapper(component, owner, showSnackbar)
                                is RootComponent.Child.EditTag -> EditTagScreenWrapper(component, owner, instance.tagId, showSnackbar)
                                is RootComponent.Child.EditRepeatCycle -> EditRepeatCycleScreenWrapper(component, owner, instance.repeatCycleId, showSnackbar)
                                is RootComponent.Child.SyncRestore -> RestoreScreenWrapper(component, owner, showSnackbar)
                                is RootComponent.Child.EditMemo -> MemoScreenWrapper(component, owner, instance.scheduleId, showSnackbar, navigateToHomeAt, isEditEntry = true)
                                is RootComponent.Child.ThemeChild -> ThemeScreenWrapper(component, owner)
                                is RootComponent.Child.WebView -> WebViewScreenWrapper(component, instance.title, instance.url)
                                is RootComponent.Child.Widget -> WidgetScreenWrapper(component, owner)
                            }
                            }
                        }
                    }
                }

                if (!windowLayout.isTablet) {
                    Scaffold(
                        snackbarHost = {
                            EbbingSnackBarHost(
                                hostState = snackbarHostState,
                                snackbar = { EbbingSnackBar(it) },
                            )
                        },
                        bottomBar = {
                            if (showBottomNav) {
                                EbbingBottomNavigationBar(
                                    selectedTab = selectedTab,
                                    onTabSelected = { tab ->
                                        // Android BottomBarTo와 동일: 홈 위에 탭 화면만 남겨 스택이 쌓이지 않게 해요
                                        component.navigateToTopLevelTab(
                                            when (tab) {
                                                BottomNavItem.HOME -> RootTab.HOME
                                                BottomNavItem.SCHEDULE -> RootTab.SCHEDULE
                                                BottomNavItem.SETTING -> RootTab.SETTING
                                            }
                                        )
                                    },
                                )
                            }
                        },
                        containerColor = EbbingTheme.colors.background,
                    ) { paddingValues ->
                        content(
                            Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        )
                    }
                } else {
                    // 태블릿: 가로는 왼쪽 레일, 세로는 하단 탭바. 온보딩에서만 숨겨요
                    val showTabletNavigation = activeChild !is RootComponent.Child.Onboarding

                    Scaffold(
                        snackbarHost = {
                            EbbingSnackBarHost(
                                hostState = snackbarHostState,
                                snackbar = { EbbingSnackBar(it) },
                            )
                        },
                        containerColor = EbbingTheme.colors.background,
                    ) { paddingValues ->
                        if (windowLayout.isLandscape) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                            ) {
                                if (showTabletNavigation) {
                                    TabletNavigationRail(
                                        selectedTab = lastTab,
                                        onTabSelected = onTabletTabSelected,
                                    )
                                }

                                // 설정 탭은 가로에서 목록(왼쪽) | 하위 화면(오른쪽) 2패널로 보여줘요
                                if (showTabletNavigation && lastTab == TopLevelTab.SETTING) {
                                    SettingScreen(
                                        viewModel = settingViewModel,
                                        isListPane = true,
                                        selectedMenu = activeChild.toSettingMenu(),
                                        // Android SettingListPane과 동일: 위아래 20dp 여백
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(vertical = 20.dp),
                                    )
                                    VerticalDivider(color = EbbingTheme.colors.strokeNormal, thickness = 1.dp)
                                }

                                content(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                            ) {
                                content(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                )

                                if (showTabletNavigation) {
                                    TabletBottomBar(
                                        selectedTab = lastTab,
                                        onTabSelected = onTabletTabSelected,
                                    )
                                }
                            }
                        }
                    }
                }

                // Android EbbingApp NetworkBanner: 네트워크가 필요한 화면(동기화·복원)에서 연결 상태를 알려요
                NetworkBanner(
                    needsNetwork = activeChild is RootComponent.Child.Sync ||
                        activeChild is RootComponent.Child.SyncRestore,
                    networkState = networkState,
                    onConnectClick = { openAppSettings() },
                    modifier = Modifier.align(Alignment.BottomCenter),
                )

                // Android MainActivity HandleAppUpdate: 웹뷰가 아닐 때 업데이트 안내를 띄워요
                if (activeChild !is RootComponent.Child.WebView) {
                    HandleAppUpdate(
                        updateState = updateState,
                        onClickUpdateInfo = {
                            snackbarScope.launch {
                                component.navigateToWebView(
                                    getString(Res.string.setting_announcement),
                                    getString(Res.string.setting_announcement_url),
                                )
                            }
                        },
                        onUpdateClick = { if (appStoreUrl.isNotBlank()) urlOpener.openUrl(appStoreUrl) },
                    )
                }
            }
        }
    }
}

private fun RootComponent.Child?.toSettingMenu(): SettingMenu? = when (this) {
    is RootComponent.Child.Tag,
    is RootComponent.Child.AddTag,
    is RootComponent.Child.EditTag -> SettingMenu.TAG
    is RootComponent.Child.RepeatCycle,
    is RootComponent.Child.AddRepeatCycle,
    is RootComponent.Child.EditRepeatCycle -> SettingMenu.REPEAT_CYCLE
    is RootComponent.Child.Sync -> SettingMenu.SYNC
    is RootComponent.Child.SyncRestore -> SettingMenu.RESTORE
    is RootComponent.Child.ThemeChild -> SettingMenu.THEME
    is RootComponent.Child.Widget -> SettingMenu.WIDGET
    else -> null
}

@Composable
private fun EbbingBottomNavigationBar(
    selectedTab: BottomNavItem?,
    onTabSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Android AppBottomBar 대응: 71dp 영역 + 상단 3dp 그림자 그라데이션, NavigationBar 68dp.
    // Android는 바깥에서 navigationBarsPadding을 주므로 바 자체 인셋은 0, 하단 안전영역은 별도로 채워요.
    val shadowColor = EbbingTheme.colors.textOnBackground
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(71.dp)
                .drawBehind {
                    val shadowHeight = 3.dp.toPx()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, shadowColor.copy(alpha = 0.1f)),
                            startY = 0f,
                            endY = shadowHeight,
                        ),
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, shadowHeight),
                    )
                },
        ) {
            NavigationBar(
                containerColor = EbbingTheme.colors.background,
                windowInsets = WindowInsets(0, 0, 0, 0),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(68.dp),
            ) {
                BottomNavItem.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(top = 2.dp),
                            ) {
                                Icon(
                                    painter = painterResource(tab.iconRes),
                                    contentDescription = stringResource(tab.labelRes),
                                    modifier = Modifier.size(32.dp),
                                )
                                Text(
                                    text = stringResource(tab.labelRes),
                                    style = EbbingTheme.typography.captionR12,
                                )
                            }
                        },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EbbingTheme.colors.textOnBackground,
                            unselectedIconColor = EbbingTheme.colors.textDisabled,
                            selectedTextColor = EbbingTheme.colors.textOnBackground,
                            unselectedTextColor = EbbingTheme.colors.textDisabled,
                            indicatorColor = Color.Transparent,
                        ),
                        interactionSource = remember { NoRippleInteractionSource() },
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(EbbingTheme.colors.background)
                .windowInsetsBottomHeight(WindowInsets.navigationBars),
        )
    }
}

/** Android NoRippleInteractionSource 대응: 하단 탭 터치 리플 제거 */
private class NoRippleInteractionSource : androidx.compose.foundation.interaction.MutableInteractionSource {
    override val interactions: kotlinx.coroutines.flow.Flow<androidx.compose.foundation.interaction.Interaction> =
        kotlinx.coroutines.flow.emptyFlow()

    override suspend fun emit(interaction: androidx.compose.foundation.interaction.Interaction) {}

    override fun tryEmit(interaction: androidx.compose.foundation.interaction.Interaction): Boolean = true
}

// 각 Wrapper는 ViewModel을 child 보관소(owner)에 두어, 하위 화면(태그/반복주기 추가 등)에 다녀와도 입력 상태가 유지돼요.
// child가 스택에서 빠지면 보관소가 clear되며 viewModelScope도 취소돼요 (Android 백스택 엔트리 ViewModel 대응).

@Composable
private fun TagScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    onShowSnackbar: (String) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val alarmRescheduler = koinInject<AlarmRescheduler>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        TagViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToAddTag = { component.navigateToAddTag() },
            onNavigateToEditTag = { tagId -> component.navigateToEditTag(tagId) },
            analyticsHelper = analyticsHelper,
            alarmRescheduler = alarmRescheduler,
            onShowSnackbar = onShowSnackbar,
        )
    }
    TagScreen(viewModel = viewModel)
}

@Composable
private fun MemoScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    scheduleId: Int,
    onShowSnackbar: (String) -> Unit,
    onNavigateToHomeAt: (LocalDate, Boolean) -> Unit,
    isEditEntry: Boolean,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        MemoViewModel(
            scheduleId = scheduleId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date -> onNavigateToHomeAt(date, false) },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            analyticsHelper = analyticsHelper,
            isEditEntry = isEditEntry,
        )
    }
    MemoScreen(viewModel = viewModel)
}

@Composable
private fun RepeatCycleScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    onShowSnackbar: (String) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        RepeatCycleViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToAddRepeatCycle = { component.navigateToAddRepeatCycle() },
            onNavigateToEditRepeatCycle = { id -> component.navigateToEditRepeatCycle(id) },
            onShowSnackbar = onShowSnackbar,
            analyticsHelper = analyticsHelper,
        )
    }
    RepeatCycleScreen(viewModel = viewModel)
}

@Composable
private fun SyncScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    onShowSnackbar: (String) -> Unit,
) {
    val syncRepository = koinInject<com.tgyuu.shared.domain.repository.SyncRepository>()
    val networkMonitor = koinInject<NetworkMonitor>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        SyncViewModel(
            syncRepository = syncRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToRestore = { component.navigateToSyncRestore() },
            onShowSnackbar = onShowSnackbar,
            analyticsHelper = analyticsHelper,
            networkMonitor = networkMonitor,
        )
    }
    SyncScreen(viewModel = viewModel)
}

@Composable
private fun RestoreScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    onShowSnackbar: (String) -> Unit,
) {
    val syncRepository = koinInject<com.tgyuu.shared.domain.repository.SyncRepository>()
    val networkMonitor = koinInject<NetworkMonitor>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        com.tgyuu.shared.ui.feature.sync.restore.RestoreViewModel(
            syncRepository = syncRepository,
            onNavigateBack = { component.onBack() },
            onShowSnackbar = onShowSnackbar,
            analyticsHelper = analyticsHelper,
            networkMonitor = networkMonitor,
        )
    }
    com.tgyuu.shared.ui.feature.sync.restore.RestoreScreen(viewModel = viewModel)
}

@Composable
private fun OnboardingScreenWrapper(
    onboardingComponent: OnboardingComponent,
    owner: ViewModelStoreOwner,
) {
    val viewModel = retainedViewModel(owner) {
        OnboardingViewModel(
            // Android TopLevelTo와 동일: 온보딩을 스택에서 지우고 홈만 남겨요
            onNavigateToHome = { onboardingComponent.onOnboardingComplete() },
        )
    }
    OnboardingScreen(viewModel = viewModel)
}

@Composable
private fun AddTodoScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    selectedDateString: String,
    onShowSnackbar: (String) -> Unit,
    onNavigateToHomeAt: (LocalDate, Boolean) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val notificationScheduler = koinInject<com.tgyuu.shared.platform.NotificationScheduler>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val alarmRescheduler = koinInject<AlarmRescheduler>()
    val viewModel = retainedViewModel(owner) {
        val selectedDate = runCatching { LocalDate.parse(selectedDateString) }.getOrElse { LocalDate.now() }
        AddTodoViewModel(
            selectedDate = selectedDate,
            todoRepository = todoRepository,
            configRepository = configRepository,
            notificationScheduler = notificationScheduler,
            analyticsHelper = analyticsHelper,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date, isFirstTodo -> onNavigateToHomeAt(date, isFirstTodo) },
            onNavigateToAddTag = { component.navigateToAddTag() },
            onNavigateToAddRepeatCycle = { component.navigateToAddRepeatCycle() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            alarmRescheduler = alarmRescheduler,
        )
    }
    AddTodoScreen(viewModel = viewModel)
}

@Composable
private fun EditTodoScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    scheduleId: Int,
    onShowSnackbar: (String) -> Unit,
    onNavigateToHomeAt: (LocalDate, Boolean) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val alarmRescheduler = koinInject<AlarmRescheduler>()
    val viewModel = retainedViewModel(owner) {
        EditTodoViewModel(
            scheduleId = scheduleId,
            todoRepository = todoRepository,
            configRepository = configRepository,
            alarmRescheduler = alarmRescheduler,
            analyticsHelper = analyticsHelper,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date -> onNavigateToHomeAt(date, false) },
            onNavigateToAddTag = { component.navigateToAddTag() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
        )
    }
    EditTodoScreen(viewModel = viewModel)
}

@Composable
private fun EditDateScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    infoId: Int,
    onShowSnackbar: (String) -> Unit,
    onNavigateToHomeAt: (LocalDate, Boolean) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val alarmRescheduler = koinInject<AlarmRescheduler>()
    val viewModel = retainedViewModel(owner) {
        EditDateViewModel(
            alarmRescheduler = alarmRescheduler,
            infoId = infoId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date -> onNavigateToHomeAt(date, false) },
            onNavigateToAddRepeatCycle = { component.navigateToAddRepeatCycle() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            configRepository = configRepository,
        )
    }
    EditDateScreen(viewModel = viewModel)
}

@Composable
private fun AddTagScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    onShowSnackbar: (String) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        AddTagViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            analyticsHelper = analyticsHelper,
        )
    }
    AddTagScreen(viewModel = viewModel)
}

@Composable
private fun AddRepeatCycleScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    onShowSnackbar: (String) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        AddRepeatCycleViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            analyticsHelper = analyticsHelper,
        )
    }
    AddRepeatCycleScreen(viewModel = viewModel)
}

@Composable
private fun EditTagScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    tagId: Int,
    onShowSnackbar: (String) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        EditTagViewModel(
            tagId = tagId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            analyticsHelper = analyticsHelper,
        )
    }
    EditTagScreen(viewModel = viewModel)
}

@Composable
private fun EditRepeatCycleScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
    repeatCycleId: Int,
    onShowSnackbar: (String) -> Unit,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val viewModel = retainedViewModel(owner) {
        EditRepeatCycleViewModel(
            repeatCycleId = repeatCycleId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onShowSnackbar = onShowSnackbar,
            experimentRepository = experimentRepository,
            analyticsHelper = analyticsHelper,
        )
    }
    EditRepeatCycleScreen(viewModel = viewModel)
}

@Composable
private fun ThemeScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
) {
    val configRepository = koinInject<ConfigRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = retainedViewModel(owner) {
        ThemeViewModel(
            configRepository = configRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    ThemeScreen(viewModel = viewModel)
}

@Composable
private fun WebViewScreenWrapper(
    component: RootComponent,
    title: String,
    url: String,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        com.tgyuu.shared.designsystem.component.EbbingSubTopBar(
            title = title,
            onNavigationClick = { component.onBack() },
            rightComponent = {},
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        com.tgyuu.shared.platform.PlatformWebView(
            url = url,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun WidgetScreenWrapper(
    component: RootComponent,
    owner: ViewModelStoreOwner,
) {
    val configRepository = koinInject<ConfigRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = retainedViewModel(owner) {
        com.tgyuu.shared.ui.feature.setting.widget.WidgetViewModel(
            configRepository = configRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    com.tgyuu.shared.ui.feature.setting.widget.WidgetScreen(viewModel = viewModel)
}

/** Android 화면별 screenName 매핑 (AnalyticsEvent.View 대응). 대응 없는 화면은 null 반환해 미로깅. */
private fun screenNameFor(child: RootComponent.Child?): String? = when (child) {
    is RootComponent.Child.Home -> "Home"
    is RootComponent.Child.Schedule -> "Schedule"
    is RootComponent.Child.Setting -> "Setting"
    is RootComponent.Child.Tag -> "Tag"
    is RootComponent.Child.RepeatCycle -> "RepeatCycle"
    is RootComponent.Child.Sync -> "SyncMain"
    is RootComponent.Child.AddTodo -> "AddTodo"
    is RootComponent.Child.EditTodo -> "EditTodo"
    is RootComponent.Child.EditDate -> "EditDate"
    is RootComponent.Child.AddTag -> "AddTag"
    is RootComponent.Child.EditTag -> "EditTag"
    is RootComponent.Child.AddRepeatCycle -> "AddRepeatCycle"
    is RootComponent.Child.EditRepeatCycle -> "EditRepeatCycle"
    is RootComponent.Child.SyncRestore -> "RestoreByDeviceId"
    is RootComponent.Child.Memo -> "AddMemo"
    is RootComponent.Child.EditMemo -> "EditMemo"
    is RootComponent.Child.ThemeChild -> "Theme"
    is RootComponent.Child.Widget -> "Widget"
    else -> null // Onboarding/WebView 등은 Android에 대응 View 없음
}

/** 스낵바 요청 직후(같은 흐름의 화면 이동)인지 판단해, 이동 시 새 스낵바까지 닫지 않도록 해요 */
private class SnackbarRequestTracker {
    private var lastRequest: TimeMark? = null

    fun mark() {
        lastRequest = TimeSource.Monotonic.markNow()
    }

    fun isFresh(): Boolean = lastRequest?.let { it.elapsedNow() < FRESH_WINDOW } ?: false

    private companion object {
        val FRESH_WINDOW = 500.milliseconds
    }
}
