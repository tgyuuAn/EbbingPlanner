package com.tgyuu.shared.ui.feature.home.addtodo

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.DefaultRepeatCycles
import com.tgyuu.shared.domain.model.DefaultTodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class AddTodoViewModel(
    private val selectedDate: LocalDate,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onNavigateToAddTag: () -> Unit = {},
    private val onNavigateToAddRepeatCycle: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val onShowTagBottomSheet: (() -> Unit)? = null,
    private val onShowRepeatCycleBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState(selectedDate = selectedDate)) {

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        safeScope.launch {
            // Set default tag and repeat cycle
            setState {
                copy(
                    tag = DefaultTodoTag.toUiModel(),
                    repeatCycle = DefaultRepeatCycles.first().toUiModel(),
                )
            }
            loadTags()
            loadRepeatCycles()
        }
    }

    private suspend fun loadTags() {
        val tags = todoRepository.loadTags()
        setState {
            copy(tagList = tags.map { it.toUiModel() }.toImmutableList())
        }
    }

    private suspend fun loadRepeatCycles() {
        val repeatCycles = todoRepository.loadRepeatCycles()
        val allRepeatCycles = DefaultRepeatCycles + repeatCycles
        setState {
            copy(repeatCycleList = allRepeatCycles.map { it.toUiModel() }.toImmutableList())
        }
    }

    override suspend fun processIntent(intent: AddTodoIntent) {
        when (intent) {
            AddTodoIntent.OnBackClick -> onNavigateBack()
            is AddTodoIntent.OnSelectedDateChange -> setState { copy(selectedDate = intent.selectedDate) }
            is AddTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is AddTodoIntent.OnPriorityChange -> onPriorityChange(intent.priority)
            AddTodoIntent.OnTagDropDownClick -> onShowTagBottomSheet?.invoke()
            is AddTodoIntent.OnTagChange -> setState { copy(tag = intent.tag) }
            AddTodoIntent.OnAddTagClick -> onNavigateToAddTag()
            AddTodoIntent.OnRepeatCycleDropDownClick -> onShowRepeatCycleBottomSheet?.invoke()
            is AddTodoIntent.OnRepeatCycleChange -> setState { copy(repeatCycle = intent.repeatCycle) }
            AddTodoIntent.OnAddRepeatCycleClick -> onNavigateToAddRepeatCycle()
            is AddTodoIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            AddTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onPriorityChange(priority: String) {
        if (priority.isNotEmpty() && !priority.all { it.isDigit() }) return
        if (priority.length >= 4) return
        setState { copy(priority = priority) }
    }

    private fun onRestDayChange(restDay: DayOfWeek) {
        val origin = currentState.restDays.toMutableSet()
        val newRestDays = if (origin.contains(restDay)) {
            origin - restDay
        } else {
            origin + restDay
        }

        if (newRestDays.size == DayOfWeek.entries.size) {
            onShowSnackbar("모든 요일을 휴식할 수는 없습니다")
            return
        }

        setState { copy(restDays = newRestDays.toImmutableSet()) }
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            onShowSnackbar("필수 항목을 작성해주세요")
            return
        }

        val tag = currentState.tag ?: return

        try {
            val schedules = currentState.schedules
            if (schedules.isEmpty()) {
                onShowSnackbar("스케줄이 없습니다. 반복 주기를 확인해주세요.")
                return
            }
            todoRepository.addTodo(
                title = currentState.title,
                dates = schedules,
                tagId = tag.id,
                priority = currentState.priority.toIntOrNull(),
                restDays = currentState.restDays.toSet(),
            )

            onShowSnackbar("새로운 일정을 추가하였습니다")
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar("일정 추가에 실패했습니다: ${e.message}")
        }
    }

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )

    private fun com.tgyuu.shared.domain.model.RepeatCycle.toUiModel(): RepeatCycleUiModel {
        return RepeatCycleUiModel(
            id = id,
            intervals = intervals.toImmutableList(),
            displayName = toDisplayName(),
        )
    }
}
