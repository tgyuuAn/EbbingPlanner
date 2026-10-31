package com.tgyuu.shared.navigation

/**
 * 하단 탭 하위 스택 저장/복원 (Android BottomBarTo: popUpTo(HomeRoute) { saveState = true } + restoreState = true).
 *
 * Android Navigation 은 홈 위의 엔트리를 꺼낼 때 '처음 꺼낸 목적지' 기준으로 상태를 저장하고,
 * 같은 목적지로 restoreState 이동하면 그 스택을 되살린다. 그래서
 *  - 일정/설정 탭 위의 하위 화면은 탭을 다시 누르면 그대로 복원되고
 *  - 홈 탭은 꺼내지는 엔트리가 아니라서 홈을 누르면 항상 홈 루트로 돌아간다.
 * 이 클래스는 그 규칙을 Decompose 설정 목록으로 재현한다. 복원된 설정은 새 child 로 만들어지므로
 * ViewModel 은 (Android saveState 처럼 SavedState 는 없이) 새로 생성된다.
 */
internal class TabStackSaver {
    private val saved = mutableMapOf<RootTab, List<Configuration>>()

    fun navigate(stack: List<Configuration>, tab: RootTab): List<Configuration> {
        val homeIndex = stack.indexOfFirst { it is Configuration.Home }
        val home = stack.getOrNull(homeIndex) as? Configuration.Home ?: Configuration.Home()
        val above = if (homeIndex >= 0) stack.drop(homeIndex + 1) else emptyList()

        // 현재 탭 하위 스택 저장 (꺼내지는 첫 화면이 탭 루트일 때만 탭으로 다시 복원할 수 있다)
        above.firstOrNull()?.toTab()?.let { owner -> saved[owner] = above }

        return when (tab) {
            RootTab.HOME -> listOf(home)
            RootTab.SCHEDULE -> listOf(home) + (saved.remove(RootTab.SCHEDULE) ?: listOf(Configuration.Schedule))
            RootTab.SETTING -> listOf(home) + (saved.remove(RootTab.SETTING) ?: listOf(Configuration.Setting))
        }
    }

    fun clear() = saved.clear()

    private fun Configuration.toTab(): RootTab? = when (this) {
        Configuration.Schedule -> RootTab.SCHEDULE
        Configuration.Setting -> RootTab.SETTING
        else -> null
    }
}
