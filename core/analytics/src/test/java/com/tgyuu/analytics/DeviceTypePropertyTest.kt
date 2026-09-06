package com.tgyuu.analytics

import com.tgyuu.deviceinfo.DeviceType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DeviceTypePropertyTest {

    @Test
    fun `프로퍼티가 없는 이벤트에도 device_type이 추가된다`() {
        // given
        val properties: Map<String, Any?>? = null

        // when
        val result = properties.withDeviceType(DeviceType.TABLET)

        // then
        assertEquals(mapOf<String, Any?>(DEVICE_TYPE_KEY to "tablet"), result)
    }

    @Test
    fun `기존 프로퍼티를 유지한 채 device_type만 덧붙인다`() {
        // given
        val properties = mapOf<String, Any?>("todo_count" to 3)

        // when
        val result = properties.withDeviceType(DeviceType.PHONE)

        // then
        assertEquals(
            mapOf<String, Any?>("todo_count" to 3, DEVICE_TYPE_KEY to "phone"),
            result,
        )
    }

    @Test
    fun `호출 지점이 직접 지정한 device_type 값은 덮어쓰지 않는다`() {
        // given
        val properties = mapOf<String, Any?>(DEVICE_TYPE_KEY to "tablet")

        // when
        val result = properties.withDeviceType(DeviceType.PHONE)

        // then
        assertEquals(mapOf<String, Any?>(DEVICE_TYPE_KEY to "tablet"), result)
    }
}
