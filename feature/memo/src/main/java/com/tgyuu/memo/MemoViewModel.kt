package com.tgyuu.memo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.memo.contract.MemoIntent
import com.tgyuu.memo.contract.MemoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<MemoState, MemoIntent>(MemoState()) {

    init {
        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId)

            setState { copy(originSchedule = originSchedule) }
        }
    }

    override suspend fun processIntent(intent: MemoIntent) {
        when (intent) {
            else -> Unit
        }
    }
}
