package com.tgyuu.repeatcycle.graph.editrepeatcycle

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class EditRepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditRepeatCycleState, EditRepeatCycleIntent>(EditRepeatCycleState()) {

    override suspend fun processIntent(intent: EditRepeatCycleIntent) {
        when (intent) {
            is EditRepeatCycleIntent.OnRepeatCycleChange -> onMemoChange(intent.memo)
            is EditRepeatCycleIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            EditRepeatCycleIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            EditRepeatCycleIntent.OnUpdateClick -> updateMemo()
        }
    }

    private fun onMemoChange(repeatCycle: String) {
        setState { copy(intervals = repeatCycle) }
    }

    private suspend fun onRestDayChange(restDay: DayOfWeek) {
        val origin = currentState.restDays

        val newRestDays = if (origin.contains(restDay)) {
            origin - restDay
        } else {
            origin + restDay
        }

        if (newRestDays.size == DayOfWeek.entries.size) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("모든 요일을 휴식할 수는 없습니다"))
            return
        }

        setState { copy(restDays = newRestDays) }
    }

    private suspend fun updateMemo() {
        if (currentState.intervals.isEmpty()) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기를 수정하였습니다"))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
