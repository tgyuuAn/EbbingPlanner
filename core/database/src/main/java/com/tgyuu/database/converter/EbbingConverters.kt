package com.tgyuu.database.converter

import androidx.room.TypeConverter
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.common.toLocalDateTimeOrThrow
import java.time.LocalDate
import java.time.LocalDateTime

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
    fun fromIntList(list: List<Int>?): String? =
        list?.joinToString(",")

    @TypeConverter
    fun toIntList(data: String?): List<Int> =
        data?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()
}
