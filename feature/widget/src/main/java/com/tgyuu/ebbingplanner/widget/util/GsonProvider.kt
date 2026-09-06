package com.tgyuu.ebbingplanner.widget.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.datetime.LocalDate
import java.lang.reflect.Type

object GsonProvider {
    val gson: Gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDate::class.java,
            object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
                override fun serialize(
                    src: LocalDate,
                    typeOfSrc: Type,
                    context: JsonSerializationContext
                ) =
                    JsonPrimitive(src.toString())

                override fun deserialize(
                    json: JsonElement,
                    typeOfT: Type,
                    context: JsonDeserializationContext
                ) =
                    LocalDate.parse(json.asString)
            }
        ).create()
}
