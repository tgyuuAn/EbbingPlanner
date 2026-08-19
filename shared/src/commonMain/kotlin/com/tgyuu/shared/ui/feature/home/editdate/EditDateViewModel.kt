package com.tgyuu.shared.ui.feature.home.editdate
import androidx.lifecycle.viewModelScope

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.loadRepeatCyclesByUsage
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.DefaultRepeatCycles
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_required_fields
import org.jetbrains.compose.resources.getString
import ebbingplanner.shared.generated.resources.snack_all_rest_days
import ebbingplanner.shared.generated.resources.snack_date_repeat_changed
import ebbingplanner.shared.generated.resources.snack_loading_schedule_info
import ebbingplanner.shared.generated.resources.snack_no_schedule_to_save
import ebbingplanner.shared.generated.resources.snack_todo_update_failed
import com.tgyuu.shared.designsystem.model.toDisplayName

class EditDateViewModel(
    private val infoId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onNavigateToAddRepeatCycle: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val configRepository: ConfigRepository,
    private val onShowDateBottomSheet: (() -> Unit)? = null,
    private val onShowRepeatCycleBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<EditDateState, EditDateIntent>(EditDateState()) {

    private var originSchedules: List<TodoSchedule> = emptyList()

    init {
        loadInitialData()
        safeScope.launch {
            configRepository.getMondayStart().collect { setState { copy(mondayStart = it) } }
        }
    }

    private fun loadInitialData() {
        safeScope.launch {
            // Set default repeat cycle
            val defaultRepeatCycle = DefaultRepeatCycles.first().toUiModel()
            setState {
                copy(repeatCycle = defaultRepeatCycle)
            }

            val result = todoRepository.loadSchedulesByTodoInfo(infoId)
            val todoInfo = todoRepository.loadTodoInfoById(infoId)

            result.firstOrNull()?.let {
                setState {
                    copy(
                        title = it.title,
                        originTagColor = it.color,
                        tagId = it.tagId,
                        selectedDate = it.date,
                        restDays = todoInfo.restDays.toImmutableSet(),
                        isPinned = it.isPinned,
                    )
                }
            }

            originSchedules = result
            loadRepeatCycles()
        }
    }

    private suspend fun loadRepeatCycles() {
        val models = todoRepository.loadRepeatCyclesByUsage(configRepository)
            .map { it.toUiModel() }.toImmutableList()
        setState {
            copy(repeatCycleList = models)
        }
    }

    override suspend fun processIntent(intent: EditDateIntent) {
        when (intent) {
            EditDateIntent.OnBackClick -> onNavigateBack()
            EditDateIntent.OnSelectedDateDropDownClick -> onShowDateBottomSheet?.invoke()
            is EditDateIntent.OnSelectedDateChange -> setState { copy(selectedDate = intent.selectedDate) }
            EditDateIntent.OnRepeatCycleDropDownClick -> onShowRepeatCycleBottomSheet?.invoke()
            is EditDateIntent.OnRepeatCycleChange -> setState { copy(repeatCycle = intent.repeatCycle) }
            EditDateIntent.OnAddRepeatCycleClick -> onNavigateToAddRepeatCycle()
            is EditDateIntent.OnRestDayChange -> onRestDayChange(intent.restDay)
            is EditDateIntent.OnPinnedChange -> setState { copy(isPinned = intent.isPinned) }
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
            viewModelScope.launch { onShowSnackbar(getString(Res.string.snack_all_rest_days)) }
            return
        }

        setState { copy(restDays = newRestDays.toImmutableSet()) }
    }

    private suspend fun onSaveClick(isDoneSchedules: List<Boolean>) {
        if (currentState.schedules.isEmpty()) {
            onShowSnackbar(getString(Res.string.snack_no_schedule_to_save))
            return
        }

        val tagId = currentState.tagId ?: run {
            onShowSnackbar(getString(Res.string.snack_loading_schedule_info))
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
                tagId = tagId,
                isPinned = currentState.isPinned,
                restDays = currentState.restDays.toSet(),
            )

            // 저장 완료 후 부가 기록 실패가 완료 흐름을 막지 않도록 격리
            runCatching {
                currentState.repeatCycle?.let { configRepository.recordRepeatCycleUsage(it.id) }
            }

            onShowSnackbar(getString(Res.string.snack_date_repeat_changed))
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_todo_update_failed))
        }
    }

    private suspend fun com.tgyuu.shared.domain.model.RepeatCycle.toUiModel(): RepeatCycleUiModel {
        return RepeatCycleUiModel(
            id = id,
            intervals = intervals.toImmutableList(),
            displayName = toDisplayName(),
        )
    }
}
