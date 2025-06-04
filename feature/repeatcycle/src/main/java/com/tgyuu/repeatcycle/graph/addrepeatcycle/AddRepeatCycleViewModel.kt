package com.tgyuu.memo.graph.addmemo

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.InputState
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AddRepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
) : BaseViewModel<AddRepeatCycleState, AddRepeatCycleIntent>(AddRepeatCycleState()) {

    override suspend fun processIntent(intent: AddRepeatCycleIntent) {
        when (intent) {
            is AddRepeatCycleIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            is AddRepeatCycleIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            AddRepeatCycleIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            AddRepeatCycleIntent.OnSaveClick -> saveRepeatCycle()
        }
    }

    private fun onRepeatCycleChange(repeatCycle: String) {
        setState { copy(repeatCycle = repeatCycle) }
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


    private suspend fun saveRepeatCycle() {
        if (currentState.repeatCycleInputState != InputState.DEFAULT) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기를 추가하였습니다"))
        navigationBus.navigate(NavigationEvent.Up)
    }
}
