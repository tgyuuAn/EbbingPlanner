package com.tgyuu.ebbingplanner.ui.widget.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object GsonProvider {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDate::class.java,
            object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
                private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
                override fun serialize(
                    src: LocalDate,
                    typeOfSrc: Type,
                    context: JsonSerializationContext
                ) =
                    JsonPrimitive(src.format(fmt))

                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type,
                    context: JsonDeserializationContext
                ) =
                    LocalDate.parse(json.asString, fmt)
            }
        ).create()
}
