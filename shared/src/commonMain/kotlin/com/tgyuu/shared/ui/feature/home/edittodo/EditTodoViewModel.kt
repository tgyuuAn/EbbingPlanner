package com.tgyuu.shared.ui.feature.home.edittodo

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class EditTodoViewModel(
    private val scheduleId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val onShowTagBottomSheet: (() -> Unit)? = null,
    private val onShowDateBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        loadExperimentVariant()
        loadScheduleData()
    }

    private fun loadScheduleData() {
        safeScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId) ?: run {
                onNavigateBack()
                return@launch
            }

            val originTagDeferred = async { todoRepository.loadTag(originSchedule.tagId) }
            val sameInfoSchedulesDeferred =
                async { todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId) }
            val todoInfoDeferred = async { todoRepository.loadTodoInfoById(originSchedule.infoId) }

            val originTag = originTagDeferred.await()
            val schedulesByDateMap = sameInfoSchedulesDeferred.await()
            val todoInfo = todoInfoDeferred.await()

            setState {
                copy(
                    originSchedule = originSchedule,
                    schedulesByDateMap = schedulesByDateMap
                        .groupBy { it.date }
                        .mapValues { (_, list) -> list.map { it.toUiModel() }.toImmutableList() }
                        .toImmutableMap(),
                    selectedDate = originSchedule.date,
                    title = originSchedule.title,
                    priority = originSchedule.priority.takeIf { it != 0 }?.toString() ?: "",
                    tag = originTag?.toUiModel(),
                    restDays = todoInfo.restDays.toImmutableSet(),
                )
            }
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val tags = todoRepository.loadTags()
        setState {
            copy(tagList = tags.map { it.toUiModel() }.toImmutableList())
        }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> onNavigateBack()
            EditTodoIntent.OnSelectedDateDropDownClick -> onShowDateBottomSheet?.invoke()
            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPriorityChange -> onPriorityChange(intent.priority)
            EditTodoIntent.OnTagDropDownClick -> onShowTagBottomSheet?.invoke()
            is EditTodoIntent.OnTagChange -> setState { copy(tag = intent.tag) }
            EditTodoIntent.OnAddTagClick -> { /* Navigate to add tag */ }
            EditTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onSelectedDateChange(date: LocalDate) {
        if (date == currentState.selectedDate) return

        val scheduledDates: Set<LocalDate> = currentState.schedulesByDateMap[date]
            ?.map { it.date }
            ?.toSet()
            ?: emptySet()

        if (date in scheduledDates) {
            onShowSnackbar("이미 해당 날짜에 일정이 있습니다.")
            return
        }

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private fun onPriorityChange(priority: String) {
        if (priority.isNotEmpty() && !priority.all { it.isDigit() }) return
        if (priority.length >= 4) return
        setState { copy(priority = priority) }
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            onShowSnackbar("필수 항목을 작성해주세요")
            return
        }

        val tag = currentState.tag ?: return
        val originSchedule = currentState.originSchedule ?: return
        val newSchedule = originSchedule.copy(
            title = currentState.title,
            date = currentState.selectedDate,
            tagId = tag.id,
            name = tag.name,
            color = tag.color,
            priority = currentState.priority.toIntOrNull() ?: 0,
        )

        try {
            todoRepository.updateTodo(newSchedule)
            todoRepository.updateTodoInfo(newSchedule, currentState.restDays.toSet())

            onShowSnackbar("일정을 업데이트 하였습니다")
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar("일정 수정에 실패했습니다")
        }
    }

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )

    private fun com.tgyuu.shared.domain.model.TodoSchedule.toUiModel() = TodoScheduleUiModel(
        id = id,
        infoId = infoId,
        date = date,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        priority = priority,
        isDone = isDone,
        memo = memo,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )

    private fun loadExperimentVariant() {
        safeScope.launch {
            val variant = experimentRepository?.getVariant(Experiment.SaveButtonPosition)
                ?: Experiment.SaveButtonPosition.Variant.CONTROL
            setState { copy(saveButtonPositionVariant = variant) }
        }
    }
}
