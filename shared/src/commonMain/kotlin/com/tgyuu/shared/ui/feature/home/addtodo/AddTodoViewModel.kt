package com.tgyuu.shared.ui.feature.home.addtodo
import androidx.lifecycle.viewModelScope

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ExperimentRepository
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
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_all_rest_days
import ebbingplanner.shared.generated.resources.snack_no_schedule_check_cycle
import ebbingplanner.shared.generated.resources.snack_required_fields
import ebbingplanner.shared.generated.resources.snack_todo_add_failed
import ebbingplanner.shared.generated.resources.snack_todo_added
import org.jetbrains.compose.resources.getString
import com.tgyuu.shared.designsystem.model.toDisplayName

class AddTodoViewModel(
    private val selectedDate: LocalDate,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onNavigateToAddTag: () -> Unit = {},
    private val onNavigateToAddRepeatCycle: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val configRepository: ConfigRepository? = null,
    private val onShowTagBottomSheet: (() -> Unit)? = null,
    private val onShowRepeatCycleBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState(selectedDate = selectedDate)) {

    init {
        loadExperimentVariant()
        loadInitialData()
    }

    private fun loadInitialData() {
        safeScope.launch {
            // Set default tag and repeat cycle
            val defaultRepeatCycle = DefaultRepeatCycles.first().toUiModel()
            setState {
                copy(
                    tag = DefaultTodoTag.toUiModel(),
                    repeatCycle = defaultRepeatCycle,
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
        val models = buildList { for (cycle in allRepeatCycles) add(cycle.toUiModel()) }
        setState {
            copy(repeatCycleList = models.toImmutableList())
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
            viewModelScope.launch { onShowSnackbar(getString(Res.string.snack_all_rest_days)) }
            return
        }

        setState { copy(restDays = newRestDays.toImmutableSet()) }
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            onShowSnackbar(getString(Res.string.snack_required_fields))
            return
        }

        val tag = currentState.tag ?: return

        try {
            val schedules = currentState.schedules
            if (schedules.isEmpty()) {
                onShowSnackbar(getString(Res.string.snack_no_schedule_check_cycle))
                return
            }
            todoRepository.addTodo(
                title = currentState.title,
                dates = schedules,
                tagId = tag.id,
                priority = currentState.priority.toIntOrNull(),
                restDays = currentState.restDays.toSet(),
            )

            configRepository?.markFirstTodoAdded()

            onShowSnackbar(getString(Res.string.snack_todo_added))
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_todo_add_failed, e.message ?: ""))
        }
    }

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )

    private suspend fun com.tgyuu.shared.domain.model.RepeatCycle.toUiModel(): RepeatCycleUiModel {
        return RepeatCycleUiModel(
            id = id,
            intervals = intervals.toImmutableList(),
            displayName = toDisplayName(),
        )
    }

    private fun loadExperimentVariant() {
        safeScope.launch {
            val variant = experimentRepository?.getVariant(Experiment.SaveButtonPosition)
                ?: Experiment.SaveButtonPosition.Variant.CONTROL
            setState { copy(saveButtonPositionVariant = variant) }
        }
    }
}
