package com.tgyuu.home.graph.add

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.home.graph.add.contract.AddTodoIntent
import com.tgyuu.home.graph.add.contract.AddTodoState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState()) {

    init {
        val dateStr = savedStateHandle.get<String>("selectedDate")
            ?: throw IllegalArgumentException("selectedDate가 없습니다.")

        setState { copy(selectedDate = dateStr.toLocalDateOrThrow()) }
    }

    override suspend fun processIntent(event: AddTodoIntent) {
        TODO("Not yet implemented")
    }
}
