package com.tgyuu.shared.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
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

    private fun child(config: Configuration, childComponentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Configuration.Home -> RootComponent.Child.Home(
                homeComponentFactory(childComponentContext, config)
            )
            is Configuration.Schedule -> RootComponent.Child.Schedule(
                scheduleComponentFactory(childComponentContext)
            )
            is Configuration.Setting -> RootComponent.Child.Setting(
                settingComponentFactory(childComponentContext)
            )
            is Configuration.Tag -> RootComponent.Child.Tag(
                tagComponentFactory(childComponentContext)
            )
            is Configuration.Memo -> RootComponent.Child.Memo(
                memoComponentFactory(childComponentContext),
                config.scheduleId,
            )
            is Configuration.RepeatCycle -> RootComponent.Child.RepeatCycle(
                repeatCycleComponentFactory(childComponentContext)
            )
            is Configuration.Sync -> RootComponent.Child.Sync(
                syncComponentFactory(childComponentContext)
            )
            is Configuration.Onboarding -> RootComponent.Child.Onboarding(
                onboardingComponentFactory(childComponentContext) { navigateToHome() }
            )
            is Configuration.AddTodo -> RootComponent.Child.AddTodo(config.selectedDate)
            is Configuration.EditTodo -> RootComponent.Child.EditTodo(config.scheduleId)
            is Configuration.EditDate -> RootComponent.Child.EditDate(config.infoId)
            is Configuration.AddTag -> RootComponent.Child.AddTag
            is Configuration.AddRepeatCycle -> RootComponent.Child.AddRepeatCycle
            is Configuration.EditTag -> RootComponent.Child.EditTag(config.tagId)
            is Configuration.EditRepeatCycle -> RootComponent.Child.EditRepeatCycle(config.repeatCycleId)
            is Configuration.Connect -> RootComponent.Child.Connect
            is Configuration.EditMemo -> RootComponent.Child.EditMemo(config.scheduleId)
            is Configuration.Theme -> RootComponent.Child.ThemeChild
            is Configuration.WebView -> RootComponent.Child.WebView(config.title, config.url)
            is Configuration.Notification -> RootComponent.Child.Notification
        }

    override fun onBack() {
        navigation.pop()
    }

    override fun navigateToHome() {
        navigation.bringToFront(Configuration.Home())
    }

    override fun navigateToSchedule() {
        navigation.bringToFront(Configuration.Schedule)
    }

    override fun navigateToSetting() {
        navigation.bringToFront(Configuration.Setting)
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

    override fun navigateToConnect() {
        navigation.pushNew(Configuration.Connect)
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

    override fun navigateToNotification() {
        navigation.pushNew(Configuration.Notification)
    }
}
