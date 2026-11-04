package com.tgyuu.analytics

import com.tgyuu.deviceinfo.DeviceType

internal const val DEVICE_TYPE_KEY = "device_type"

/**
 * 이벤트 프로퍼티에 기기 형태를 나타내는 [DEVICE_TYPE_KEY] 항목을 추가합니다.
 *
 * 모든 분석 이벤트가 동일한 기준으로 태블릿과 휴대폰을 구분할 수 있도록,
 * 각 호출 지점이 아니라 로그를 실제로 전송하는 지점에서 일괄적으로 붙입니다.
 * 이벤트가 이미 같은 키를 가지고 있다면 호출 지점이 지정한 값을 그대로 유지합니다.
 */
internal fun Map<String, Any?>?.withDeviceType(deviceType: DeviceType): Map<String, Any?> {
    val properties = this ?: emptyMap()
    if (properties.containsKey(DEVICE_TYPE_KEY)) return properties
    return properties + (DEVICE_TYPE_KEY to deviceType.value)
}
