package com.tgyuu.database

import com.tgyuu.database.converter.EbbingConverters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlinx.datetime.DayOfWeek

class ConvertersTest {
    private val converters = EbbingConverters()

    @Test
    fun `빈 Set은 빈 문자열로 변환된다`() {
        val result = converters.fromRestDays(emptySet())
        assertEquals("", result)
    }

    @Test
    fun `null은 빈 문자열로 변환된다`() {
        val result = converters.fromRestDays(null)
        assertEquals("", result)
    }

    @Test
    fun `DayOfWeek Set이 쉼표로 구분된 문자열로 변환된다`() {
        val restDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val result = converters.fromRestDays(restDays)

        // Set은 순서 보장 안하므로 split 후 비교
        val numbers = result.split(",").map { it.toInt() }.sorted()
        assertEquals(listOf(1, 3, 5), numbers)
    }

    @Test
    fun `빈 문자열은 빈 Set으로 변환된다`() {
        val result = converters.toRestDays("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `null 문자열은 빈 Set으로 변환된다`() {
        val result = converters.toRestDays(null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `쉼표로 구분된 문자열이 DayOfWeek Set으로 변환된다`() {
        val result = converters.toRestDays("1,3,5")

        assertEquals(3, result.size)
        assertTrue(result.contains(DayOfWeek.MONDAY))
        assertTrue(result.contains(DayOfWeek.WEDNESDAY))
        assertTrue(result.contains(DayOfWeek.FRIDAY))
    }

    @Test
    fun `잘못된 형식은 무시하고 유효한 값만 변환된다`() {
        val result = converters.toRestDays("1,abc,3,999,5")

        assertEquals(3, result.size)
        assertTrue(result.contains(DayOfWeek.MONDAY))
        assertTrue(result.contains(DayOfWeek.WEDNESDAY))
        assertTrue(result.contains(DayOfWeek.FRIDAY))
    }

    @Test
    fun `범위 밖 숫자는 무시된다`() {
        val result = converters.toRestDays("0,1,8,3")

        assertEquals(2, result.size)
        assertTrue(result.contains(DayOfWeek.MONDAY))
        assertTrue(result.contains(DayOfWeek.WEDNESDAY))
    }

    @Test
    fun `라운드트립 변환이 정상 동작한다`() {
        val original = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.SATURDAY)
        val serialized = converters.fromRestDays(original)
        val deserialized = converters.toRestDays(serialized)

        assertEquals(original, deserialized)
    }
}
