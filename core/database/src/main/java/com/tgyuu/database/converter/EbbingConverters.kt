package com.tgyuu.database.converter

import androidx.room.TypeConverter
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toLocalDateOrThrow
import java.time.LocalDate

class EbbingConverters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toFormattedString()
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.toLocalDateOrThrow()
    }
}
