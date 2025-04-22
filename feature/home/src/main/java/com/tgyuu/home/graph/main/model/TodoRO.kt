package com.tgyuu.home.graph.main.model

import java.time.LocalDate

data class TodoRO(
    val id: Int,
    val infoId: Int,
    val title: String,
    val description: String,
    val tagId: Int,
    val name: String,
    val color: Int,
    val date: LocalDate,
    val memo: String,
    val priority: Int,
    val isDone: Boolean,
    val createdAt: LocalDate,
    val updatedAt: LocalDate,
)
