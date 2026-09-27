package com.tgyuu.deviceinfo

/**
 * 분석 로그에 기록할 기기 형태입니다.
 *
 * [value]는 Amplitude 이벤트 프로퍼티에 그대로 실리는 문자열이므로 임의로 변경하면 안 됩니다.
 */
enum class DeviceType(val value: String) {
    PHONE("phone"),
    TABLET("tablet"),
}
