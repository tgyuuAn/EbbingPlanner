package com.tgyuu.shared.domain

import com.tgyuu.shared.domain.model.UpdateInfo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppUpdatePolicyTest {
    private fun info(min: String) = UpdateInfo(minVersion = min, noticeMsg = "")

    @Test
    fun `다이얼로그는 앞자리부터 비교한다`() {
        assertTrue(AppUpdatePolicy.shouldShowUpdateDialog("1.2.3", info("1.2.4")))
        assertTrue(AppUpdatePolicy.shouldShowUpdateDialog("1.2", info("1.3.0")))
        assertFalse(AppUpdatePolicy.shouldShowUpdateDialog("1.3.0", info("1.2.9")))
        assertFalse(AppUpdatePolicy.shouldShowUpdateDialog("2.0.0", info("1.9.9")))
        assertFalse(AppUpdatePolicy.shouldShowUpdateDialog("1.2.3", info("1.2.3")))
    }

    @Test
    fun `정보가 없거나 현재 버전을 모르면 띄우지 않는다`() {
        assertFalse(AppUpdatePolicy.shouldShowUpdateDialog("1.0.0", null))
        assertFalse(AppUpdatePolicy.shouldShowUpdateDialog("", info("9.9.9")))
        // Remote Config 값이 없을 때의 기본값(1.0.0)은 업데이트를 요구하지 않는다
        assertFalse(AppUpdatePolicy.shouldShowUpdateDialog("1.0.0", info("1.0.0")))
    }

    @Test
    fun `설정 버튼은 Android 와 같이 자리별로 하나라도 작으면 보인다`() {
        assertTrue(AppUpdatePolicy.shouldShowUpdateButton("1.2.3", info("1.2.4")))
        // Android SettingScreen 비교 그대로: 2.0.0 < 1.1.0 의 둘째 자리 → true
        assertTrue(AppUpdatePolicy.shouldShowUpdateButton("2.0.0", info("1.1.0")))
        assertFalse(AppUpdatePolicy.shouldShowUpdateButton("1.2.3", info("1.2.3")))
        assertFalse(AppUpdatePolicy.shouldShowUpdateButton("1.2.3", null))
    }
}
