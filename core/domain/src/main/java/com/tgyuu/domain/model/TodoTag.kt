package com.tgyuu.domain.model

import java.time.LocalDate

data class TodoTag(
    val id: Int,
    val name: String,
    val color: Int,
    val startDate: LocalDate,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)

val DefaultTodoTag = TodoTag(
    id = 0,
    name = "미지정",
    color = 0XFFBBE1FA.toInt(),
    startDate = LocalDate.now(),
    createdAt = LocalDate.now(),
    updatedAt = LocalDate.now()
)
