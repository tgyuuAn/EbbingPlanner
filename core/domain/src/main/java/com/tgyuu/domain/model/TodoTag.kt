package com.tgyuu.domain.model

import java.time.LocalDate

data class TodoTag(
    val id: Int,
    val name: String,
    val color: Int,
    val createdAt: LocalDate,
)

val DefaultTodoTag = TodoTag(
    id = 1,
    name = "미지정",
    color = 0XFFBBE1FA.toInt(),
    createdAt = LocalDate.now(),
)
