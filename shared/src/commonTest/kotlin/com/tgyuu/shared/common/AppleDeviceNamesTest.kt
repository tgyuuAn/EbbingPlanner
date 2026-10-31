package com.tgyuu.shared.common

import kotlin.test.Test
import kotlin.test.assertEquals

class AppleDeviceNamesTest {

    @Test
    fun `알려진 식별자는 마케팅 모델명으로 바꾼다`() {
        assertEquals("iPhone 14 Pro", AppleDeviceNames.displayName("iPhone15,2", "iPhone"))
        assertEquals("iPad Pro 11-inch (M4)", AppleDeviceNames.displayName("iPad16,3", "iPad"))
    }

    @Test
    fun `표에 없는 식별자는 식별자를 그대로 보여준다`() {
        assertEquals("iPhone99,1", AppleDeviceNames.displayName("iPhone99,1", "iPhone"))
    }

    @Test
    fun `식별자를 못 읽으면 일반 이름으로 폴백한다`() {
        assertEquals("iPhone", AppleDeviceNames.displayName("", "iPhone"))
    }
}
