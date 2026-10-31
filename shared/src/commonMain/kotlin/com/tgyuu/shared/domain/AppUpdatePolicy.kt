package com.tgyuu.shared.domain

import com.tgyuu.shared.domain.model.UpdateInfo

/**
 * 앱 업데이트 안내 판단 (Android 버전 비교 로직을 그대로 옮김).
 *
 * Android 는 두 곳에서 서로 다른 비교를 쓴다. 동작을 맞추기 위해 둘 다 그대로 옮겼다.
 *  - 시작 다이얼로그(app HandleAppUpdate.checkShouldUpdate): 앞자리부터 사전식 비교
 *  - 설정 버전 행(feature/setting SettingScreen.checkShouldUpdate): 세 자리 중 하나라도 작으면 true
 */
object AppUpdatePolicy {

    /** 시작 시 소프트/하드 업데이트 다이얼로그를 띄울지 (Android HandleAppUpdate.shouldUpdate) */
    fun shouldShowUpdateDialog(currentVersion: String, info: UpdateInfo?): Boolean {
        if (info == null || currentVersion.isBlank()) return false
        val current = normalizeVersion(currentVersion)
        val min = normalizeVersion(info.minVersion)
        for ((cur, m) in current.zip(min)) {
            if (cur < m) return true
            if (cur > m) return false
        }
        return false
    }

    /** 설정 버전 행에 업데이트 버튼을 보여줄지 (Android SettingScreen.isShowUpdateButton) */
    fun shouldShowUpdateButton(currentVersion: String, info: UpdateInfo?): Boolean {
        if (info == null || currentVersion.isBlank()) return false
        val current = normalizeVersion(currentVersion)
        val min = normalizeVersion(info.minVersion)
        // Android 는 (0..2) 인덱스를 바로 읽어 자릿수가 모자라면 크래시한다. 여기선 0으로 본다.
        return (0..2).any { (current.getOrNull(it) ?: 0) < (min.getOrNull(it) ?: 0) }
    }

    private fun normalizeVersion(version: String): List<Int> = version.split('.')
        .map { it.toIntOrNull() ?: 0 }
        .let { if (it.size == 2) it + 0 else it }
}
