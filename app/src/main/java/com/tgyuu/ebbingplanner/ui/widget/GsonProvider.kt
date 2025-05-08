// com.tgyuu.home.widget.GsonProvider.kt
package com.tgyuu.ebbingplanner.ui.widget

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.gson.*
import java.lang.reflect.Type

object GsonProvider {
  val gson: Gson = GsonBuilder()
    .registerTypeAdapter(
      LocalDate::class.java,
      object : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        private val fmt = DateTimeFormatter.ISO_LOCAL_DATE
        override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext) =
          JsonPrimitive(src.format(fmt))
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext) =
          LocalDate.parse(json.asString, fmt)
      }
    )
    .create()
}
