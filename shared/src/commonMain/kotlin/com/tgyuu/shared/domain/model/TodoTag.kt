package com.tgyuu.shared.domain.model

import com.tgyuu.shared.common.now
import kotlinx.datetime.LocalDate

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
