package com.tgyuu.shared.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** 위젯 딥링크 (Android 위젯 actionStartActivity 대응) */
sealed interface WidgetDeepLink {
    /** 앱만 열기 */
    data object Open : WidgetDeepLink

    /** 해당 날짜가 선택된 홈 (Android openHomeAction) */
    data class Home(val date: String) : WidgetDeepLink

    /** 해당 날짜로 일정 등록 (Android AddTodoFromWidgetAction) */
    data class AddTodo(val date: String) : WidgetDeepLink

    companion object {
        /** ebbingplanner://home?date=yyyy-MM-dd 형식을 해석해요 */
        fun parse(url: String): WidgetDeepLink? {
            if (!url.contains("://")) return null
            val withoutScheme = url.substringAfter("://")
            val host = withoutScheme.substringBefore('?').trimEnd('/')
            val date = withoutScheme.substringAfter("date=", missingDelimiterValue = "")
                .substringBefore('&')
                .takeIf { Regex("\\d{4}-\\d{2}-\\d{2}").matches(it) }
            return when (host) {
                "home" -> date?.let { Home(it) } ?: Open
                "addtodo" -> date?.let { AddTodo(it) } ?: Open
                "open" -> Open
                else -> null
            }
        }
    }
}

/**
 * 플랫폼(iOS Swift)에서 들어오는 앱 외부 이벤트를 RootContent로 전달해요.
 * - 위젯 딥링크: 콜드 스타트에도 놓치지 않도록 마지막 1개를 보관하고, 처리 후 비워요
 * - 포그라운드 복귀: 위젯에서 바꾼 완료 상태 등 외부 변경을 다시 불러오게 해요
 */
object AppExternalEvents {
    private val _deepLinks = MutableSharedFlow<WidgetDeepLink>(replay = 1, extraBufferCapacity = 1)
    val deepLinks: SharedFlow<WidgetDeepLink> = _deepLinks.asSharedFlow()

    private val _foreground = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val foreground: SharedFlow<Unit> = _foreground.asSharedFlow()

    fun emitDeepLink(link: WidgetDeepLink) {
        _deepLinks.tryEmit(link)
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    fun consumeDeepLink() {
        _deepLinks.resetReplayCache()
    }

    fun emitForeground() {
        _foreground.tryEmit(Unit)
    }
}
