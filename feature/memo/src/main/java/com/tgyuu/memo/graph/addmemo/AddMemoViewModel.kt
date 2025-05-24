package com.tgyuu.memo.graph.addmemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.InputState.Companion.getStringInputState
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.memo.graph.addmemo.contract.AddMemoIntent
import com.tgyuu.memo.graph.addmemo.contract.AddMemoState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddMemoState, AddMemoIntent>(AddMemoState()) {

    init {
        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId)

            setState { copy(originSchedule = originSchedule) }
        }
    }

    override suspend fun processIntent(intent: AddMemoIntent) {
        when (intent) {
            is AddMemoIntent.OnMemoChange -> onMemoChange(intent.memo)
            AddMemoIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            AddMemoIntent.OnSaveClick -> saveMemo()
        }
    }

    private fun onMemoChange(memo: String) {
        setState { copy(memo = memo) }
    }

    private suspend fun saveMemo() {
        val newState = currentState.copy(
            memoInputState = getStringInputState(currentState.memo.trim())
        )

        if (newState.isInputFieldIncomplete) {
            setState { newState }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        todoRepository.updateTodo(currentState.originSchedule?.copy(memo = newState.memo) ?: return)
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("메모를 추가하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(newState.originSchedule!!.date.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
