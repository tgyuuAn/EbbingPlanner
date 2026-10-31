package com.tgyuu.shared.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.essenty.backhandler.BackHandler
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    fun onBack()
    fun navigateToHome()
    fun navigateToSchedule()
    fun navigateToSetting()

    /**
     * 하위 화면에서 탭을 눌러도 스택이 쌓이지 않도록 홈 위에 탭 화면만 남기고 이동해요.
     * 일정·설정 탭의 하위 화면은 탭별로 저장했다가 다시 그 탭을 누르면 복원해요
     * (Android BottomBarTo: popUpTo(HomeRoute){saveState} + restoreState 대응, 폰/태블릿 공통)
     */
    fun navigateToTopLevelTab(tab: RootTab)

    /** 저장/복원 없이 홈 루트만 남겨요 (저장 후 홈 이동, 위젯 딥링크 등) */
    fun popToHome()
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
    fun navigateToSyncRestore()
    fun navigateToEditMemo(scheduleId: Int)
    fun navigateToTheme()
    fun navigateToWebView(title: String, url: String)
    fun navigateToWidget()

    /** 각 화면은 자기 ViewModel 보관소를 가지고, 스택에서 빠질 때 함께 정리돼요 */
    sealed class Child(
        val viewModelStoreOwner: ViewModelStoreOwner,
        /** 화면이 뒤로가기(iOS 엣지 스와이프)를 가로챌 때 쓰는 child 전용 BackHandler */
        val backHandler: BackHandler,
    ) {
        class Home(val component: HomeComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Schedule(val component: ScheduleComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Setting(val component: SettingComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Tag(val component: TagComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Memo(val component: MemoComponent, val scheduleId: Int, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class RepeatCycle(val component: RepeatCycleComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Sync(val component: SyncComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Onboarding(val component: OnboardingComponent, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class AddTodo(val selectedDate: String, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class EditTodo(val scheduleId: Int, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class EditDate(val infoId: Int, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class AddTag(owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class AddRepeatCycle(owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class EditTag(val tagId: Int, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class EditRepeatCycle(val repeatCycleId: Int, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class SyncRestore(owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class EditMemo(val scheduleId: Int, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class ThemeChild(owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class WebView(val title: String, val url: String, owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
        class Widget(owner: ViewModelStoreOwner, backHandler: BackHandler) : Child(owner, backHandler)
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
    data object SyncRestore : Configuration

    @Serializable
    data class EditMemo(val scheduleId: Int) : Configuration

    @Serializable
    data object Theme : Configuration

    @Serializable
    data class WebView(val title: String, val url: String) : Configuration

    @Serializable
    data object Widget : Configuration
}

enum class RootTab { HOME, SCHEDULE, SETTING }
