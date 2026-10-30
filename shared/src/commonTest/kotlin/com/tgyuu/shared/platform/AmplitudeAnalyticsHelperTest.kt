package com.tgyuu.shared.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AmplitudeAnalyticsHelperTest {

    private val tracked = mutableListOf<Pair<String, Map<String, Any?>>>()
    private var lastUserId: String? = "unset"

    private val helper = AmplitudeAnalyticsHelper(
        onTrack = { eventType, properties -> tracked += eventType to properties },
        onSetUserId = { lastUserId = it },
    )

    @Test
    fun screenView_는_Android_View_이벤트명으로_변환된다() {
        helper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.SCREEN_VIEW,
                properties = mapOf(AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "Home"),
            )
        )

        assertEquals("View_Home", tracked.single().first)
        assertEquals(emptyMap(), tracked.single().second)
    }

    @Test
    fun buttonClick_은_버튼명_화면명_순서로_변환되고_추가_속성은_유지된다() {
        helper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.BUTTON_CLICK,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "AddTodo",
                    AnalyticsEvent.PropertiesKeys.BUTTON_NAME to "Save",
                    "tag" to "study",
                ),
            )
        )

        assertEquals("Click_Save_AddTodo", tracked.single().first)
        assertEquals(mapOf<String, Any?>("tag" to "study"), tracked.single().second)
    }

    @Test
    fun action_은_결과가_있으면_뒤에_붙는다() {
        helper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.ACTION,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "Sync",
                    AnalyticsEvent.PropertiesKeys.ACTION_NAME to "Upload",
                    AnalyticsEvent.PropertiesKeys.ACTION_RESULT to "Success",
                ),
            )
        )
        helper.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.ACTION,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "Sync",
                    AnalyticsEvent.PropertiesKeys.ACTION_NAME to "Upload",
                ),
            )
        )

        assertEquals("Action_Upload_Sync_Success", tracked[0].first)
        assertEquals("Action_Upload_Sync", tracked[1].first)
        assertEquals(emptyMap(), tracked[0].second)
    }

    @Test
    fun 알_수_없는_타입은_그대로_전달된다() {
        helper.logEvent(AnalyticsEvent(type = "custom_event", properties = mapOf("k" to 1)))

        assertEquals("custom_event", tracked.single().first)
        assertEquals(mapOf<String, Any?>("k" to 1), tracked.single().second)
    }

    @Test
    fun userId_에는_ios_접두사가_붙고_null_은_그대로_해제된다() {
        helper.setUserId("abc-123")
        assertEquals("ios-abc-123", lastUserId)

        helper.setUserId(null)
        assertNull(lastUserId)
    }
}
