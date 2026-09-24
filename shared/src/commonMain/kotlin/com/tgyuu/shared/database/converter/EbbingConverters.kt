package com.tgyuu.shared.database.converter

import androidx.room.TypeConverter
import com.tgyuu.shared.common.toCsv
import com.tgyuu.shared.common.toFormattedString
import com.tgyuu.shared.common.toIntListFromCsv
import com.tgyuu.shared.common.toLocalDateOrThrow
import com.tgyuu.shared.common.toLocalDateTimeOrThrow
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

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

    // --- LocalDateTime ---
    @TypeConverter
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.toFormattedString()
    }

    @TypeConverter
    fun toLocalDateTime(dateString: String?): LocalDateTime? {
        return dateString?.toLocalDateTimeOrThrow()
    }

    // --- List<Int> ---
    @TypeConverter
    fun fromIntList(list: List<Int>?): String? = list?.toCsv()

    @TypeConverter
    fun toIntList(data: String?): List<Int> = data.toIntListFromCsv()

    // --- Set<DayOfWeek> ---
    @TypeConverter
    fun fromRestDays(value: Set<DayOfWeek>?): String {
        return value?.joinToString(",") { (it.ordinal + 1).toString() } ?: ""
    }

    @TypeConverter
    fun toRestDays(value: String?): Set<DayOfWeek> {
        if (value.isNullOrEmpty()) return emptySet()
        return value.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..7 }
            .map { DayOfWeek.entries[it - 1] }
            .toSet()
    }
}
