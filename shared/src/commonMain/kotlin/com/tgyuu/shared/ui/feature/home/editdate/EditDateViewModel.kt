package com.tgyuu.shared.ui.feature.home.editdate

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.DefaultRepeatCycles
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

class EditDateViewModel(
    private val infoId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val onShowDateBottomSheet: (() -> Unit)? = null,
    private val onShowRepeatCycleBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<EditDateState, EditDateIntent>(EditDateState()) {

    private var originSchedules: List<TodoSchedule> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        safeScope.launch {
            // Set default repeat cycle
            setState {
                copy(repeatCycle = DefaultRepeatCycles.first().toUiModel())
            }

            val result = todoRepository.loadSchedulesByTodoInfo(infoId)
            val todoInfo = todoRepository.loadTodoInfoById(infoId)

            result.firstOrNull()?.let {
                setState {
                    copy(
                        title = it.title,
                        originTagColor = it.color,
                        selectedDate = it.date,
                        restDays = todoInfo.restDays.toImmutableSet(),
                    )
                }
            }

            originSchedules = result
            loadRepeatCycles()
        }
    }

    private suspend fun loadRepeatCycles() {
        val repeatCycles = todoRepository.loadRepeatCycles()
        val allRepeatCycles = DefaultRepeatCycles + repeatCycles
        setState {
            copy(repeatCycleList = allRepeatCycles.map { it.toUiModel() }.toImmutableList())
        }
    }

    override suspend fun processIntent(intent: EditDateIntent) {
        when (intent) {
            EditDateIntent.OnBackClick -> onNavigateBack()
            EditDateIntent.OnSelectedDateDropDownClick -> onShowDateBottomSheet?.invoke()
            is EditDateIntent.OnSelectedDateChange -> setState { copy(selectedDate = intent.selectedDate) }
            EditDateIntent.OnRepeatCycleDropDownClick -> onShowRepeatCycleBottomSheet?.invoke()
            is EditDateIntent.OnRepeatCycleChange -> setState { copy(repeatCycle = intent.repeatCycle) }
            EditDateIntent.OnAddRepeatCycleClick -> { /* Navigate to add repeat cycle */ }
            is EditDateIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is EditDateIntent.OnSaveClick -> onSaveClick(intent.isDoneSchedules)
        }
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

    private suspend fun onSaveClick(isDoneSchedules: List<Boolean>) {
        if (currentState.schedules.isEmpty()) {
            onShowSnackbar("저장할 일정이 없습니다")
            return
        }

        try {
            // Delete original schedules
            originSchedules.firstOrNull()
                ?.infoId
                ?.let { todoRepository.deleteTodoByTodoInfo(it) }

            // Add new schedules
            todoRepository.addTodo(
                title = currentState.title,
                dates = currentState.schedules,
                isDoneSchedules = isDoneSchedules,
                tagId = originSchedules.firstOrNull()?.tagId ?: 0,
                priority = originSchedules.firstOrNull()?.priority,
                restDays = currentState.restDays.toSet(),
            )

            onShowSnackbar("해당 일정의 날짜 및 반복 주기를 변경하였습니다")
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar("일정 수정에 실패했습니다")
        }
    }

    private fun com.tgyuu.shared.domain.model.RepeatCycle.toUiModel(): RepeatCycleUiModel {
        return RepeatCycleUiModel(
            id = id,
            intervals = intervals.toImmutableList(),
            displayName = toDisplayName(),
        )
    }
}
