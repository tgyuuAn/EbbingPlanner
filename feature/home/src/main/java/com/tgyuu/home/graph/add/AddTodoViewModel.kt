package com.tgyuu.home.graph.add

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.domain.RepeatCycle
import com.tgyuu.domain.TodoTag
import com.tgyuu.home.graph.add.contract.AddTodoIntent
import com.tgyuu.home.graph.add.contract.AddTodoState
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState()) {

    init {
        val dateStr = savedStateHandle.get<String>("selectedDate")
            ?: throw IllegalArgumentException("selectedDate가 없습니다.")

        setState { copy(selectedDate = dateStr.toLocalDateOrThrow()) }
    }

    override suspend fun processIntent(intent: AddTodoIntent) {
        when (intent) {
            AddTodoIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            is AddTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                EbbingEvent.ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is AddTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is AddTodoIntent.OnRepeatCycleDropDownClick -> eventBus.sendEvent(
                EbbingEvent.ShowBottomSheet(intent.content)
            )

            is AddTodoIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            is AddTodoIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is AddTodoIntent.OnTagChange -> onTagChange(intent.tag)
            AddTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private suspend fun onSelectedDateChange(date: LocalDate) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onTagChange(tag: TodoTag) {
        //Todo
    }

    private suspend fun onRepeatCycleChange(repeatCycle: RepeatCycle) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(repeatCycle = repeatCycle) }
    }

    private suspend fun onRestDayChange(restDay: DayOfWeek) {
        val origin = state.value.restDays

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

    private fun onSaveClick() {
        //Todo
    }
}
