package com.tgyuu.memo.graph.editmemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.memo.graph.editmemo.contract.EditMemoIntent
import com.tgyuu.memo.graph.editmemo.contract.EditMemoState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditMemoState, EditMemoIntent>(EditMemoState()) {

    init {
        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId)
            setState {
                copy(
                    originSchedule = originSchedule,
                    memo = originSchedule.memo,
                )
            }
        }
    }

    override suspend fun processIntent(intent: EditMemoIntent) {
        when (intent) {
            is EditMemoIntent.OnMemoChange -> onMemoChange(intent.memo)
            EditMemoIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            EditMemoIntent.OnUpdateClick -> updateMemo()
        }
    }

    private fun onMemoChange(memo: String) {
        setState { copy(memo = memo) }
    }

    private suspend fun updateMemo() {
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        todoRepository.updateTodo(
            currentState.originSchedule?.copy(memo = currentState.memo) ?: return
        )
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("메모를 추가하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(currentState.originSchedule!!.date.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
