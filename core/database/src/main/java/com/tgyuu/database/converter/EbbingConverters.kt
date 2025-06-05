package com.tgyuu.database.converter

import androidx.room.TypeConverter
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toLocalDateOrThrow
import java.time.DayOfWeek
import java.time.LocalDate

class EbbingConverters {
    // --- LocalDate ---
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toFormattedString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.toLocalDateOrThrow()
    }

    // --- List<Int> ---
    @TypeConverter
    fun fromIntList(list: List<Int>?): String? =
        list?.joinToString(",")

    @TypeConverter
    fun toIntList(data: String?): List<Int> =
        data?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()

    // --- List<DayOfWeek> ---
    @TypeConverter
    fun fromDayOfWeekList(list: List<DayOfWeek>?): String? =
        list?.joinToString(",") { it.name }

    @TypeConverter
    fun toDayOfWeekList(data: String?): List<DayOfWeek> =
        data?.split(",")?.mapNotNull {
            runCatching { DayOfWeek.valueOf(it.trim()) }.getOrNull()
        } ?: emptyList()
}
