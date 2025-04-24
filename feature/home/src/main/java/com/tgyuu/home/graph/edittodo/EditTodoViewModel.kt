package com.tgyuu.home.graph.edittodo

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EbbingEvent.ShowBottomSheet
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.toLocalDateOrThrow
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.model.TodoTag
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.InputState.Companion.getStringInputState
import com.tgyuu.home.graph.addtodo.contract.AddTodoIntent
import com.tgyuu.home.graph.edittodo.contract.EditTodoIntent
import com.tgyuu.home.graph.edittodo.contract.EditTodoState
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditTodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        val dateStr = savedStateHandle.get<String>("selectedDate")
            ?: throw IllegalArgumentException("선택된 날짜가 없습니다.")

        setState { copy(selectedDate = dateStr.toLocalDateOrThrow()) }
    }

    internal fun loadTags() = viewModelScope.launch {
        val loadedTagList = todoRepository.loadTagList()
        setState { copy(tagList = loadedTagList) }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            is EditTodoIntent.OnSelectedDataChangeClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPriorityChange -> onPriorityChange(intent.priority)
            is EditTodoIntent.OnRepeatCycleDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            is EditTodoIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is EditTodoIntent.OnTagDropDownClick -> eventBus.sendEvent(
                ShowBottomSheet(intent.content)
            )

            is EditTodoIntent.OnTagChange -> onTagChange(intent.tag)
            AddTodoIntent.OnAddTagClick -> onAddTagClick()
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

    private fun onPriorityChange(priority: String) {
        if (!priority.isDigitsOnly()) return
        if (priority.length >= 4) return

        setState { copy(priority = priority) }
    }

    private suspend fun onTagChange(todoTag: TodoTag) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

        setState { copy(tag = todoTag) }
    }

    private suspend fun onRepeatCycleChange(repeatCycle: RepeatCycle) {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)

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

    private suspend fun onAddTagClick() {
        eventBus.sendEvent(EbbingEvent.HideBottomSheet)
        navigationBus.navigate(NavigationEvent.To(HomeGraph.AddTagRoute))
    }

    private suspend fun onSaveClick() {
        val newState = currentState.copy(
            titleInputState = getStringInputState(currentState.title.trim())
        )

        if (newState.isInputFieldIncomplete) {
            setState { newState }
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        todoRepository.addTodo(
            title = currentState.title,
            schedules = currentState.schedules,
            tagId = currentState.tag.id,
            priority = currentState.priority?.toIntOrNull(),
        )
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("새로운 일정을 추가하였습니다"))
        navigationBus.navigate(
            NavigationEvent.TopLevelTo(
                HomeGraph.HomeRoute(currentState.selectedDate.toFormattedString())
            )
        )
    }
}
