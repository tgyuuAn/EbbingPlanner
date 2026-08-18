package com.tgyuu.shared.ui

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.nav_home
import ebbingplanner.shared.generated.resources.nav_schedule
import ebbingplanner.shared.generated.resources.nav_setting
import ebbingplanner.shared.generated.resources.webview_privacy_title
import ebbingplanner.shared.generated.resources.webview_terms_title
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

    // Check if first app open → show onboarding
    LaunchedEffect(Unit) {
        if (configRepository.isFirstAppOpen()) {
            component.navigateToOnboarding()
        }
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
        )
    }

    val analyticsHelper = koinInject<com.tgyuu.shared.platform.AnalyticsHelper>()
    val webViewPrivacyTitle = stringResource(Res.string.webview_privacy_title)
    val webViewTermsTitle = stringResource(Res.string.webview_terms_title)
    val scheduleViewModel = remember {
        ScheduleViewModel(
            todoRepository = todoRepository,
            analyticsHelper = analyticsHelper,
            onNavigateToAddTodo = { date -> component.navigateToAddTodo(date) },
            onNavigateToEditTodo = { scheduleId -> component.navigateToEditTodo(scheduleId) },
            onNavigateToEditDate = { infoId -> component.navigateToEditDate(infoId) },
            onNavigateToMemo = { scheduleId -> component.navigateToMemo(scheduleId) },
            onNavigateToEditMemo = { scheduleId -> component.navigateToMemo(scheduleId) },
            onShowSnackBar = { /* TODO: snackbar host */ },
        )
    }

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
            onNavigateToNotification = { component.navigateToNotification() },
            onNavigateToWidget = { component.navigateToWidget() },
            onOpenUrl = { url ->
                val title = when {
                    url.contains("privacy") -> webViewPrivacyTitle
                    url.contains("terms") -> webViewTermsTitle
                    else -> ""
                }
                component.navigateToWebView(title, url)
            },
        )
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

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val showSnackbar: (String) -> Unit = remember(snackbarHostState) {
        { message -> snackbarScope.launch { snackbarHostState.showSnackbar(message) } }
    }

    EbbingTheme(theme = appTheme) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (showBottomNav) {
                    EbbingBottomNavigationBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            when (tab) {
                                BottomNavItem.HOME -> component.navigateToHome()
                                BottomNavItem.SCHEDULE -> component.navigateToSchedule()
                                BottomNavItem.SETTING -> component.navigateToSetting()
                            }
                        },
                    )
                }
            },
            containerColor = EbbingTheme.colors.background,
            modifier = modifier,
        ) { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                color = EbbingTheme.colors.background,
            ) {
                Children(
                    stack = component.stack,
                    animation = stackAnimation(fade()),
                ) { child ->
                    when (val instance = child.instance) {
                        is RootComponent.Child.Home -> HomeScreen(
                            viewModel = homeViewModel,
                            onRequestInAppReview = { inAppReviewManager.requestReview() },
                        )
                        is RootComponent.Child.Schedule -> ScheduleScreen(viewModel = scheduleViewModel)
                        is RootComponent.Child.Setting -> SettingScreen(viewModel = settingViewModel)
                        is RootComponent.Child.Tag -> TagScreenWrapper(component)
                        is RootComponent.Child.Memo -> MemoScreenWrapper(component, instance.scheduleId)
                        is RootComponent.Child.RepeatCycle -> RepeatCycleScreenWrapper(component)
                        is RootComponent.Child.Sync -> SyncScreenWrapper(component, showSnackbar)
                        is RootComponent.Child.Onboarding -> OnboardingScreenWrapper(component)
                        is RootComponent.Child.AddTodo -> AddTodoScreenWrapper(component, instance.selectedDate)
                        is RootComponent.Child.EditTodo -> EditTodoScreenWrapper(component, instance.scheduleId)
                        is RootComponent.Child.EditDate -> EditDateScreenWrapper(component, instance.infoId)
                        is RootComponent.Child.AddTag -> AddTagScreenWrapper(component)
                        is RootComponent.Child.AddRepeatCycle -> AddRepeatCycleScreenWrapper(component)
                        is RootComponent.Child.EditTag -> EditTagScreenWrapper(component, instance.tagId)
                        is RootComponent.Child.EditRepeatCycle -> EditRepeatCycleScreenWrapper(component, instance.repeatCycleId)
                        is RootComponent.Child.SyncRestore -> RestoreScreenWrapper(component, showSnackbar)
                        is RootComponent.Child.EditMemo -> EditMemoScreenWrapper(component, instance.scheduleId)
                        is RootComponent.Child.ThemeChild -> ThemeScreenWrapper(component)
                        is RootComponent.Child.WebView -> WebViewScreenWrapper(component, instance.title, instance.url)
                        is RootComponent.Child.Notification -> NotificationScreenWrapper(component)
                        is RootComponent.Child.Widget -> WidgetScreenWrapper(component)
                    }
                }
            }
        }
    }
}

@Composable
private fun EbbingBottomNavigationBar(
    selectedTab: BottomNavItem?,
    onTabSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        containerColor = EbbingTheme.colors.background,
        modifier = modifier.height(80.dp),
    ) {
        BottomNavItem.entries.forEach { tab ->
            val isSelected = tab == selectedTab

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            painter = painterResource(tab.iconRes),
                            contentDescription = stringResource(tab.labelRes),
                            modifier = Modifier.size(32.dp),
                        )
                        Text(
                            text = stringResource(tab.labelRes),
                            style = EbbingTheme.typography.captionM,
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EbbingTheme.colors.black,
                    unselectedIconColor = EbbingTheme.colors.dark3,
                    selectedTextColor = EbbingTheme.colors.black,
                    unselectedTextColor = EbbingTheme.colors.dark3,
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
private fun TagScreenWrapper(component: RootComponent) {
    val todoRepository = koinInject<TodoRepository>()
    val viewModel = remember {
        TagViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToAddTag = { component.navigateToAddTag() },
            onNavigateToEditTag = { tagId -> component.navigateToEditTag(tagId) },
        )
    }
    TagScreen(viewModel = viewModel)
}

@Composable
private fun MemoScreenWrapper(
    component: RootComponent,
    scheduleId: Int,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember(scheduleId) {
        MemoViewModel(
            scheduleId = scheduleId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { component.navigateToHome() },
            experimentRepository = experimentRepository,
            isEditEntry = false,
        )
    }
    MemoScreen(viewModel = viewModel)
}

@Composable
private fun RepeatCycleScreenWrapper(component: RootComponent) {
    val todoRepository = koinInject<TodoRepository>()
    val viewModel = remember {
        RepeatCycleViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToAddRepeatCycle = { component.navigateToAddRepeatCycle() },
            onNavigateToEditRepeatCycle = { id -> component.navigateToEditRepeatCycle(id) },
        )
    }
    RepeatCycleScreen(viewModel = viewModel)
}

@Composable
private fun SyncScreenWrapper(
    component: RootComponent,
    onShowSnackbar: (String) -> Unit,
) {
    val syncRepository = koinInject<com.tgyuu.shared.domain.repository.SyncRepository>()
    val viewModel = remember {
        SyncViewModel(
            syncRepository = syncRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToRestore = { component.navigateToSyncRestore() },
            onShowSnackbar = onShowSnackbar,
        )
    }
    SyncScreen(viewModel = viewModel)
}

@Composable
private fun RestoreScreenWrapper(
    component: RootComponent,
    onShowSnackbar: (String) -> Unit,
) {
    val syncRepository = koinInject<com.tgyuu.shared.domain.repository.SyncRepository>()
    val viewModel = remember {
        com.tgyuu.shared.ui.feature.sync.restore.RestoreViewModel(
            syncRepository = syncRepository,
            onNavigateBack = { component.onBack() },
            onShowSnackbar = onShowSnackbar,
        )
    }
    com.tgyuu.shared.ui.feature.sync.restore.RestoreScreen(viewModel = viewModel)
}

@Composable
private fun OnboardingScreenWrapper(component: RootComponent) {
    val viewModel = remember {
        OnboardingViewModel(
            onNavigateToHome = { component.navigateToHome() },
        )
    }
    OnboardingScreen(viewModel = viewModel)
}

@Composable
private fun AddTodoScreenWrapper(
    component: RootComponent,
    selectedDateString: String,
) {
    val todoRepository = koinInject<TodoRepository>()
    val selectedDate = remember(selectedDateString) {
        try {
            LocalDate.parse(selectedDateString)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val viewModel = remember(selectedDate) {
        AddTodoViewModel(
            selectedDate = selectedDate,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date -> component.navigateToHome() },
            onNavigateToAddTag = { component.navigateToAddTag() },
            onNavigateToAddRepeatCycle = { component.navigateToAddRepeatCycle() },
            experimentRepository = experimentRepository,
            configRepository = configRepository,
        )
    }
    AddTodoScreen(viewModel = viewModel)
}

@Composable
private fun EditTodoScreenWrapper(
    component: RootComponent,
    scheduleId: Int,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val viewModel = remember(scheduleId) {
        EditTodoViewModel(
            scheduleId = scheduleId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date -> component.navigateToHome() },
            experimentRepository = experimentRepository,
            configRepository = configRepository,
        )
    }
    EditTodoScreen(viewModel = viewModel)
}

@Composable
private fun EditDateScreenWrapper(
    component: RootComponent,
    infoId: Int,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val configRepository = koinInject<ConfigRepository>()
    val viewModel = remember(infoId) {
        EditDateViewModel(
            infoId = infoId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { date -> component.navigateToHome() },
            experimentRepository = experimentRepository,
            configRepository = configRepository,
        )
    }
    EditDateScreen(viewModel = viewModel)
}

@Composable
private fun AddTagScreenWrapper(component: RootComponent) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember {
        AddTagViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    AddTagScreen(viewModel = viewModel)
}

@Composable
private fun AddRepeatCycleScreenWrapper(component: RootComponent) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember {
        AddRepeatCycleViewModel(
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    AddRepeatCycleScreen(viewModel = viewModel)
}

@Composable
private fun EditTagScreenWrapper(
    component: RootComponent,
    tagId: Int,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember(tagId) {
        EditTagViewModel(
            tagId = tagId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    EditTagScreen(viewModel = viewModel)
}

@Composable
private fun EditRepeatCycleScreenWrapper(
    component: RootComponent,
    repeatCycleId: Int,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember(repeatCycleId) {
        EditRepeatCycleViewModel(
            repeatCycleId = repeatCycleId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    EditRepeatCycleScreen(viewModel = viewModel)
}

@Composable
private fun EditMemoScreenWrapper(
    component: RootComponent,
    scheduleId: Int,
) {
    val todoRepository = koinInject<TodoRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember(scheduleId) {
        MemoViewModel(
            scheduleId = scheduleId,
            todoRepository = todoRepository,
            onNavigateBack = { component.onBack() },
            onNavigateToHome = { component.navigateToHome() },
            experimentRepository = experimentRepository,
            isEditEntry = true,
        )
    }
    MemoScreen(viewModel = viewModel)
}

@Composable
private fun ThemeScreenWrapper(component: RootComponent) {
    val configRepository = koinInject<ConfigRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember {
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
private fun NotificationScreenWrapper(component: RootComponent) {
    val configRepository = koinInject<ConfigRepository>()
    val viewModel = remember {
        com.tgyuu.shared.ui.feature.home.notification.NotificationViewModel(
            configRepository = configRepository,
            onNavigateBack = { component.onBack() },
        )
    }
    com.tgyuu.shared.ui.feature.home.notification.NotificationScreen(viewModel = viewModel)
}

@Composable
private fun WidgetScreenWrapper(component: RootComponent) {
    val configRepository = koinInject<ConfigRepository>()
    val experimentRepository = koinInject<com.tgyuu.shared.domain.repository.ExperimentRepository>()
    val viewModel = remember {
        com.tgyuu.shared.ui.feature.setting.widget.WidgetViewModel(
            configRepository = configRepository,
            onNavigateBack = { component.onBack() },
            experimentRepository = experimentRepository,
        )
    }
    com.tgyuu.shared.ui.feature.setting.widget.WidgetScreen(viewModel = viewModel)
}
