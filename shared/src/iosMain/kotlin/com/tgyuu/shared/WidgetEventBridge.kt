package com.tgyuu.shared

import com.tgyuu.shared.platform.AnalyticsEvent
import com.tgyuu.shared.platform.AnalyticsHelper
import org.koin.mp.KoinPlatform
import platform.Foundation.NSUserDefaults

/**
 * 위젯 분석 이벤트 큐 (Android TodayTodoWidgetReceiver / CalendarWidgetReceiver 의 analyticsHelper.logEvent 대응).
 *
 * 위젯 익스텐션은 Amplitude 를 쓸 수 없어서 App Group UserDefaults 큐에 이벤트를 쌓고,
 * 앱이 시작/포그라운드 복귀 시 꺼내 AnalyticsHelper 로 보낸다.
 * 큐 형식은 iosApp/EbbingPlannerWidget/EbbingPlannerWidget.swift 의 WidgetAnalyticsQueue 와 같아야 한다.
 *   [{"type":"button_click","screen_name":"TodoWidget","button_name":"Refresh","timestamp":<ms>}, ...]
 * 이벤트 시각은 전송 시각이 된다(위젯에서 일어난 시각은 timestamp 로 큐에만 남는다).
 */
internal object WidgetAnalyticsQueue {
    const val APP_GROUP = "group.com.tgyuu.ebbingplanner"
    const val QUEUE_KEY = "widget_analytics_queue"

    fun drain() {
        val defaults = NSUserDefaults(suiteName = APP_GROUP)
        val queued = defaults.arrayForKey(QUEUE_KEY) ?: return
        defaults.removeObjectForKey(QUEUE_KEY)
        if (queued.isEmpty()) return

        val analyticsHelper = runCatching { KoinPlatform.getKoin().get<AnalyticsHelper>() }.getOrNull() ?: return
        queued.forEach { item ->
            val event = item as? Map<*, *> ?: return@forEach
            val type = event["type"] as? String ?: AnalyticsEvent.Types.BUTTON_CLICK
            val screenName = event[AnalyticsEvent.PropertiesKeys.SCREEN_NAME] as? String ?: return@forEach
            val buttonName = event[AnalyticsEvent.PropertiesKeys.BUTTON_NAME] as? String ?: return@forEach
            analyticsHelper.logEvent(
                AnalyticsEvent(
                    type = type,
                    properties = mapOf(
                        AnalyticsEvent.PropertiesKeys.SCREEN_NAME to screenName,
                        AnalyticsEvent.PropertiesKeys.BUTTON_NAME to buttonName,
                    ),
                )
            )
        }
    }
}
