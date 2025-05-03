package com.tgyuu.tag.graph.main.contract

import com.tgyuu.common.base.UiState
import com.tgyuu.domain.model.TodoTag

data class TagState(
    val isLoading: Boolean = true,
    val tagList: List<TodoTag> = emptyList(),
) : UiState
