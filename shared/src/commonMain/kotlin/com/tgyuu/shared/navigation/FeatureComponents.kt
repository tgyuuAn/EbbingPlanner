package com.tgyuu.shared.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

// Home Feature
interface HomeComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToAddTodo(selectedDate: String)
    fun navigateToEditTodo(scheduleId: Int)
    fun navigateToEditDate(infoId: Int)
    fun onBack()

    sealed class Child {
        class Main(val component: HomeMainComponent) : Child()
        class AddTodo(val component: AddTodoComponent) : Child()
        class EditTodo(val component: EditTodoComponent) : Child()
        class EditDate(val component: EditDateComponent) : Child()
    }
}

interface HomeMainComponent
interface AddTodoComponent {
    val selectedDate: String
}
interface EditTodoComponent {
    val scheduleId: Int
}
interface EditDateComponent {
    val infoId: Int
}

// Schedule Feature
interface ScheduleComponent

// Setting Feature
interface SettingComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToTheme()
    fun navigateToWidget()
    fun navigateToWebView(title: String, url: String)
    fun onBack()

    sealed class Child {
        class Main(val component: SettingMainComponent) : Child()
        class Theme(val component: ThemeComponent) : Child()
        class Widget(val component: WidgetComponent) : Child()
        class WebView(val component: WebViewComponent) : Child()
    }
}

interface SettingMainComponent
interface ThemeComponent
interface WidgetComponent
interface WebViewComponent {
    val title: String
    val url: String
}

// Tag Feature
interface TagComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToAddTag()
    fun navigateToEditTag(tagId: Int)
    fun onBack()

    sealed class Child {
        class Main(val component: TagMainComponent) : Child()
        class AddTag(val component: AddTagComponent) : Child()
        class EditTag(val component: EditTagComponent) : Child()
    }
}

interface TagMainComponent
interface AddTagComponent
interface EditTagComponent {
    val tagId: Int
}

// Memo Feature
interface MemoComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToAddMemo(scheduleId: Int? = null)
    fun navigateToEditMemo(scheduleId: Int? = null)
    fun onBack()

    sealed class Child {
        class Main(val component: MemoMainComponent) : Child()
        class AddMemo(val component: AddMemoComponent) : Child()
        class EditMemo(val component: EditMemoComponent) : Child()
    }
}

interface MemoMainComponent
interface AddMemoComponent {
    val scheduleId: Int?
}
interface EditMemoComponent {
    val scheduleId: Int?
}

// RepeatCycle Feature
interface RepeatCycleComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToAddRepeatCycle()
    fun navigateToEditRepeatCycle(repeatCycleId: Int? = null)
    fun onBack()

    sealed class Child {
        class Main(val component: RepeatCycleMainComponent) : Child()
        class AddRepeatCycle(val component: AddRepeatCycleComponent) : Child()
        class EditRepeatCycle(val component: EditRepeatCycleComponent) : Child()
    }
}

interface RepeatCycleMainComponent
interface AddRepeatCycleComponent
interface EditRepeatCycleComponent {
    val repeatCycleId: Int?
}

// Sync Feature
interface SyncComponent {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateToConnect()
    fun onBack()

    sealed class Child {
        class Main(val component: SyncMainComponent) : Child()
        class Connect(val component: ConnectComponent) : Child()
    }
}

interface SyncMainComponent
interface ConnectComponent

// Onboarding Feature
interface OnboardingComponent {
    fun onOnboardingComplete()
}
