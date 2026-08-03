package com.tgyuu.shared.ui.feature.home.edittodo
import androidx.lifecycle.viewModelScope

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.common.sortedByUsageOrder
import com.tgyuu.shared.domain.repository.ConfigRepository
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
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_date_has_schedule
import ebbingplanner.shared.generated.resources.snack_required_fields
import ebbingplanner.shared.generated.resources.snack_todo_update_failed
import ebbingplanner.shared.generated.resources.snack_todo_updated
import org.jetbrains.compose.resources.getString

class EditTodoViewModel(
    private val scheduleId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val configRepository: ConfigRepository? = null,
    private val onShowTagBottomSheet: (() -> Unit)? = null,
    private val onShowDateBottomSheet: (() -> Unit)? = null,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
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
                    isPinned = originSchedule.isPinned,
                    tag = originTag?.toUiModel(),
                    restDays = todoInfo.restDays.toImmutableSet(),
                )
            }
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val tags = todoRepository.loadTags()
        val usageOrder = configRepository?.getTagUsageOrder() ?: emptyList()
        val sortedTags = tags.sortedByUsageOrder(usageOrder) { it.id }
        setState {
            copy(tagList = sortedTags.map { it.toUiModel() }.toImmutableList())
        }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> onNavigateBack()
            EditTodoIntent.OnSelectedDateDropDownClick -> onShowDateBottomSheet?.invoke()
            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPinnedChange -> setState { copy(isPinned = intent.isPinned) }
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
            viewModelScope.launch { onShowSnackbar(getString(Res.string.snack_date_has_schedule)) }
            return
        }

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) {
            onShowSnackbar(getString(Res.string.snack_required_fields))
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
            isPinned = currentState.isPinned,
        )

        try {
            todoRepository.updateTodo(newSchedule)
            todoRepository.updateTodoInfo(newSchedule, currentState.restDays.toSet())
            // 저장 완료 후 부가 기록 실패가 완료 흐름을 막지 않도록 격리
            runCatching { configRepository?.recordTagUsage(tag.id) }

            onShowSnackbar(getString(Res.string.snack_todo_updated))
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_todo_update_failed))
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
        isPinned = isPinned,
        isDone = isDone,
        memo = memo,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}
