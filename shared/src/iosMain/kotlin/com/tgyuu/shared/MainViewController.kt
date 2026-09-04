package com.tgyuu.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.tgyuu.shared.navigation.Configuration
import com.tgyuu.shared.navigation.DefaultRootComponent
import com.tgyuu.shared.navigation.HomeComponent
import com.tgyuu.shared.navigation.MemoComponent
import com.tgyuu.shared.navigation.OnboardingComponent
import com.tgyuu.shared.navigation.RepeatCycleComponent
import com.tgyuu.shared.navigation.ScheduleComponent
import com.tgyuu.shared.navigation.SettingComponent
import com.tgyuu.shared.navigation.SyncComponent
import com.tgyuu.shared.navigation.TagComponent
import com.tgyuu.shared.ui.RootContent
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    val lifecycle = LifecycleRegistry()

    val rootComponent = DefaultRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        homeComponentFactory = { context, config ->
            object : HomeComponent {
                override val childStack: Value<ChildStack<*, HomeComponent.Child>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = Unit,
                            instance = HomeComponent.Child.Main(
                                object : com.tgyuu.shared.navigation.HomeMainComponent {}
                            ),
                        )
                    )
                override fun navigateToAddTodo(selectedDate: String) {}
                override fun navigateToEditTodo(scheduleId: Int) {}
                override fun navigateToEditDate(infoId: Int) {}
                override fun onBack() {}
            }
        },
        scheduleComponentFactory = { context ->
            object : ScheduleComponent {}
        },
        settingComponentFactory = { context ->
            object : SettingComponent {
                override val childStack: Value<ChildStack<*, SettingComponent.Child>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = Unit,
                            instance = SettingComponent.Child.Main(
                                object : com.tgyuu.shared.navigation.SettingMainComponent {}
                            ),
                        )
                    )
                override fun navigateToTheme() {}
                override fun navigateToWidget() {}
                override fun navigateToWebView(title: String, url: String) {}
                override fun onBack() {}
            }
        },
        tagComponentFactory = { context ->
            object : TagComponent {
                override val childStack: Value<ChildStack<*, TagComponent.Child>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = Unit,
                            instance = TagComponent.Child.Main(
                                object : com.tgyuu.shared.navigation.TagMainComponent {}
                            ),
                        )
                    )
                override fun navigateToAddTag() {}
                override fun navigateToEditTag(tagId: Int) {}
                override fun onBack() {}
            }
        },
        memoComponentFactory = { context ->
            object : MemoComponent {
                override val childStack: Value<ChildStack<*, MemoComponent.Child>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = Unit,
                            instance = MemoComponent.Child.Main(
                                object : com.tgyuu.shared.navigation.MemoMainComponent {}
                            ),
                        )
                    )
                override fun navigateToAddMemo(scheduleId: Int?) {}
                override fun navigateToEditMemo(scheduleId: Int?) {}
                override fun onBack() {}
            }
        },
        repeatCycleComponentFactory = { context ->
            object : RepeatCycleComponent {
                override val childStack: Value<ChildStack<*, RepeatCycleComponent.Child>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = Unit,
                            instance = RepeatCycleComponent.Child.Main(
                                object : com.tgyuu.shared.navigation.RepeatCycleMainComponent {}
                            ),
                        )
                    )
                override fun navigateToAddRepeatCycle() {}
                override fun navigateToEditRepeatCycle(repeatCycleId: Int?) {}
                override fun onBack() {}
            }
        },
        syncComponentFactory = { context ->
            object : SyncComponent {
                override val childStack: Value<ChildStack<*, SyncComponent.Child>>
                    get() = MutableValue(
                        ChildStack(
                            configuration = Unit,
                            instance = SyncComponent.Child.Main(
                                object : com.tgyuu.shared.navigation.SyncMainComponent {}
                            ),
                        )
                    )
                override fun onBack() {}
            }
        },
        onboardingComponentFactory = { context, onComplete ->
            object : OnboardingComponent {
                override fun onOnboardingComplete() = onComplete()
            }
        },
    )

    return ComposeUIViewController {
        RootContent(component = rootComponent)
    }
}
