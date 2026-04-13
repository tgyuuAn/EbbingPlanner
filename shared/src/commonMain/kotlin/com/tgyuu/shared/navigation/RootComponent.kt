package com.tgyuu.shared.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    fun onBack()
    fun navigateToHome()
    fun navigateToSchedule()
    fun navigateToSetting()
    fun navigateToTag()
    fun navigateToMemo(scheduleId: Int)
    fun navigateToRepeatCycle()
    fun navigateToSync()
    fun navigateToOnboarding()
    fun navigateToAddTodo(selectedDate: String)
    fun navigateToEditTodo(scheduleId: Int)
    fun navigateToEditDate(infoId: Int)
    fun navigateToAddTag()
    fun navigateToAddRepeatCycle()
    fun navigateToEditTag(tagId: Int)
    fun navigateToEditRepeatCycle(repeatCycleId: Int)
    fun navigateToConnect()
    fun navigateToEditMemo(scheduleId: Int)
    fun navigateToTheme()
    fun navigateToWebView(title: String, url: String)
    fun navigateToNotification()
    fun navigateToWidget()

    sealed class Child {
        class Home(val component: HomeComponent) : Child()
        class Schedule(val component: ScheduleComponent) : Child()
        class Setting(val component: SettingComponent) : Child()
        class Tag(val component: TagComponent) : Child()
        class Memo(val component: MemoComponent, val scheduleId: Int) : Child()
        class RepeatCycle(val component: RepeatCycleComponent) : Child()
        class Sync(val component: SyncComponent) : Child()
        class Onboarding(val component: OnboardingComponent) : Child()
        class AddTodo(val selectedDate: String) : Child()
        class EditTodo(val scheduleId: Int) : Child()
        class EditDate(val infoId: Int) : Child()
        data object AddTag : Child()
        data object AddRepeatCycle : Child()
        class EditTag(val tagId: Int) : Child()
        class EditRepeatCycle(val repeatCycleId: Int) : Child()
        data object Connect : Child()
        class EditMemo(val scheduleId: Int) : Child()
        data object ThemeChild : Child()
        class WebView(val title: String, val url: String) : Child()
        data object Notification : Child()
        data object Widget : Child()
    }
}

@Serializable
sealed interface Configuration {
    @Serializable
    data object Onboarding : Configuration

    @Serializable
    data class Home(
        val workedDate: String? = null,
        val showWidgetNudge: Boolean = false,
    ) : Configuration

    @Serializable
    data object Schedule : Configuration

    @Serializable
    data object Setting : Configuration

    @Serializable
    data object Tag : Configuration

    @Serializable
    data class Memo(val scheduleId: Int) : Configuration

    @Serializable
    data object RepeatCycle : Configuration

    @Serializable
    data object Sync : Configuration

    @Serializable
    data class AddTodo(val selectedDate: String) : Configuration

    @Serializable
    data class EditTodo(val scheduleId: Int) : Configuration

    @Serializable
    data class EditDate(val infoId: Int) : Configuration

    @Serializable
    data object AddTag : Configuration

    @Serializable
    data object AddRepeatCycle : Configuration

    @Serializable
    data class EditTag(val tagId: Int) : Configuration

    @Serializable
    data class EditRepeatCycle(val repeatCycleId: Int) : Configuration

    @Serializable
    data object Connect : Configuration

    @Serializable
    data class EditMemo(val scheduleId: Int) : Configuration

    @Serializable
    data object Theme : Configuration

    @Serializable
    data class WebView(val title: String, val url: String) : Configuration

    @Serializable
    data object Notification : Configuration

    @Serializable
    data object Widget : Configuration
}
