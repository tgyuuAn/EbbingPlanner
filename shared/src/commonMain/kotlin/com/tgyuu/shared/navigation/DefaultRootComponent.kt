package com.tgyuu.shared.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val homeComponentFactory: (ComponentContext, Configuration.Home) -> HomeComponent,
    private val scheduleComponentFactory: (ComponentContext) -> ScheduleComponent,
    private val settingComponentFactory: (ComponentContext) -> SettingComponent,
    private val tagComponentFactory: (ComponentContext) -> TagComponent,
    private val memoComponentFactory: (ComponentContext) -> MemoComponent,
    private val repeatCycleComponentFactory: (ComponentContext) -> RepeatCycleComponent,
    private val syncComponentFactory: (ComponentContext) -> SyncComponent,
    private val onboardingComponentFactory: (ComponentContext, () -> Unit) -> OnboardingComponent,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.Home(),
            handleBackButton = true,
            childFactory = ::child,
        )

    private fun child(config: Configuration, childComponentContext: ComponentContext): RootComponent.Child {
        val owner = childComponentContext.retainedViewModelStoreOwner()
        val back = childComponentContext.backHandler
        return when (config) {
            is Configuration.Home -> RootComponent.Child.Home(
                homeComponentFactory(childComponentContext, config), owner, back
            )
            is Configuration.Schedule -> RootComponent.Child.Schedule(
                scheduleComponentFactory(childComponentContext), owner, back
            )
            is Configuration.Setting -> RootComponent.Child.Setting(
                settingComponentFactory(childComponentContext), owner, back
            )
            is Configuration.Tag -> RootComponent.Child.Tag(
                tagComponentFactory(childComponentContext), owner, back
            )
            is Configuration.Memo -> RootComponent.Child.Memo(
                memoComponentFactory(childComponentContext),
                config.scheduleId,
                owner,
                back,
            )
            is Configuration.RepeatCycle -> RootComponent.Child.RepeatCycle(
                repeatCycleComponentFactory(childComponentContext), owner, back
            )
            is Configuration.Sync -> RootComponent.Child.Sync(
                syncComponentFactory(childComponentContext), owner, back
            )
            is Configuration.Onboarding -> RootComponent.Child.Onboarding(
                onboardingComponentFactory(childComponentContext) { navigateToHomeClearingStack() }, owner, back
            )
            is Configuration.AddTodo -> RootComponent.Child.AddTodo(config.selectedDate, owner, back)
            is Configuration.EditTodo -> RootComponent.Child.EditTodo(config.scheduleId, owner, back)
            is Configuration.EditDate -> RootComponent.Child.EditDate(config.infoId, owner, back)
            is Configuration.AddTag -> RootComponent.Child.AddTag(owner, back)
            is Configuration.AddRepeatCycle -> RootComponent.Child.AddRepeatCycle(owner, back)
            is Configuration.EditTag -> RootComponent.Child.EditTag(config.tagId, owner, back)
            is Configuration.EditRepeatCycle -> RootComponent.Child.EditRepeatCycle(config.repeatCycleId, owner, back)
            is Configuration.SyncRestore -> RootComponent.Child.SyncRestore(owner, back)
            is Configuration.EditMemo -> RootComponent.Child.EditMemo(config.scheduleId, owner, back)
            is Configuration.Theme -> RootComponent.Child.ThemeChild(owner, back)
            is Configuration.WebView -> RootComponent.Child.WebView(config.title, config.url, owner, back)
            is Configuration.Widget -> RootComponent.Child.Widget(owner, back)
        }
    }

    /** Android TopLevelTo(popUpTo(0) inclusive) 대응: 온보딩 등 이전 스택을 모두 비우고 홈만 남겨요 */
    private fun navigateToHomeClearingStack() {
        navigation.replaceAll(Configuration.Home())
    }

    override fun onBack() {
        navigation.pop()
    }

    // Android BottomBarTo/HomeRoute 이동과 동일하게 스택을 쌓지 않고 탭 루트로 정리해요
    override fun navigateToHome() = navigateToTopLevelTab(RootTab.HOME)

    override fun navigateToSchedule() = navigateToTopLevelTab(RootTab.SCHEDULE)

    override fun navigateToSetting() = navigateToTopLevelTab(RootTab.SETTING)

    override fun navigateToTopLevelTab(tab: RootTab) {
        navigation.navigate { stack ->
            val home = stack.firstOrNull { it is Configuration.Home } ?: Configuration.Home()
            when (tab) {
                RootTab.HOME -> listOf(home)
                RootTab.SCHEDULE -> listOf(home, Configuration.Schedule)
                RootTab.SETTING -> listOf(home, Configuration.Setting)
            }
        }
    }

    override fun navigateToTag() {
        navigation.pushNew(Configuration.Tag)
    }

    override fun navigateToMemo(scheduleId: Int) {
        navigation.pushNew(Configuration.Memo(scheduleId))
    }

    override fun navigateToRepeatCycle() {
        navigation.pushNew(Configuration.RepeatCycle)
    }

    override fun navigateToSync() {
        navigation.pushNew(Configuration.Sync)
    }

    override fun navigateToOnboarding() {
        navigation.replaceAll(Configuration.Onboarding)
    }

    override fun navigateToAddTodo(selectedDate: String) {
        navigation.pushNew(Configuration.AddTodo(selectedDate))
    }

    override fun navigateToEditTodo(scheduleId: Int) {
        navigation.pushNew(Configuration.EditTodo(scheduleId))
    }

    override fun navigateToEditDate(infoId: Int) {
        navigation.pushNew(Configuration.EditDate(infoId))
    }

    override fun navigateToAddTag() {
        navigation.pushNew(Configuration.AddTag)
    }

    override fun navigateToAddRepeatCycle() {
        navigation.pushNew(Configuration.AddRepeatCycle)
    }

    override fun navigateToEditTag(tagId: Int) {
        navigation.pushNew(Configuration.EditTag(tagId))
    }

    override fun navigateToEditRepeatCycle(repeatCycleId: Int) {
        navigation.pushNew(Configuration.EditRepeatCycle(repeatCycleId))
    }

    override fun navigateToSyncRestore() {
        navigation.pushNew(Configuration.SyncRestore)
    }

    override fun navigateToEditMemo(scheduleId: Int) {
        navigation.pushNew(Configuration.EditMemo(scheduleId))
    }

    override fun navigateToTheme() {
        navigation.pushNew(Configuration.Theme)
    }

    override fun navigateToWebView(title: String, url: String) {
        navigation.pushNew(Configuration.WebView(title, url))
    }

    override fun navigateToWidget() {
        navigation.pushNew(Configuration.Widget)
    }
}
