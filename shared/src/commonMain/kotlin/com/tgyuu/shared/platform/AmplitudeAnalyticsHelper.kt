package com.tgyuu.shared.platform

import com.tgyuu.shared.platform.AnalyticsEvent.PropertiesKeys

/**
 * Android core/analytics 의 AmplitudeAnalyticsHelper 대응.
 * 이벤트명을 Android 와 같은 규칙으로 만들어 같은 Amplitude 프로젝트에서 함께 집계되게 한다.
 *  - screen_view  → View_{screen}
 *  - button_click → Click_{button}_{screen}
 *  - action       → Action_{action}_{screen}[_{result}]
 * 이름에 쓰인 키는 속성에서 제외한다(Android 는 해당 값을 별도 필드로 들고 있어 속성에 넣지 않는다).
 * 실제 SDK 호출은 플랫폼(iOS Swift)이 람다로 주입한다.
 */
class AmplitudeAnalyticsHelper(
    private val onTrack: (eventType: String, properties: Map<String, Any?>) -> Unit,
    private val onSetUserId: (String?) -> Unit,
) : AnalyticsHelper {

    override fun logEvent(event: AnalyticsEvent) {
        val props = event.properties
        val screen = props[PropertiesKeys.SCREEN_NAME]
        val (eventType, consumedKeys) = when (event.type) {
            AnalyticsEvent.Types.SCREEN_VIEW ->
                "View_$screen" to setOf(PropertiesKeys.SCREEN_NAME)

            AnalyticsEvent.Types.BUTTON_CLICK ->
                "Click_${props[PropertiesKeys.BUTTON_NAME]}_$screen" to
                    setOf(PropertiesKeys.SCREEN_NAME, PropertiesKeys.BUTTON_NAME)

            AnalyticsEvent.Types.ACTION -> buildString {
                append("Action_${props[PropertiesKeys.ACTION_NAME]}_$screen")
                props[PropertiesKeys.ACTION_RESULT]?.let { append("_$it") }
            } to setOf(PropertiesKeys.SCREEN_NAME, PropertiesKeys.ACTION_NAME, PropertiesKeys.ACTION_RESULT)

            else -> event.type to emptySet()
        }

        onTrack(eventType, props - consumedKeys)
    }

    override fun setUserId(userId: String?) {
        // Android 는 "android-" 접두사를 붙인다. 같은 프로젝트에서 플랫폼을 구분하기 위해 iOS 는 "ios-".
        onSetUserId(userId?.let { "ios-$it" })
    }
}
