package com.tgyuu.shared.ui.feature.memo

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_memo_added
import ebbingplanner.shared.generated.resources.snack_memo_added_all
import ebbingplanner.shared.generated.resources.snack_memo_save_failed
import ebbingplanner.shared.generated.resources.snack_memo_updated
import ebbingplanner.shared.generated.resources.snack_memo_updated_all
import ebbingplanner.shared.generated.resources.snack_schedule_load_failed
import org.jetbrains.compose.resources.getString

class MemoViewModel(
    private val scheduleId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    isEditEntry: Boolean = false,
) : BaseViewModel<MemoState, MemoIntent>(MemoState(isEditEntry = isEditEntry)) {

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        safeScope.launch {
            try {
                val schedule = todoRepository.loadSchedule(scheduleId) ?: run {
                    onNavigateBack()
                    return@launch
                }
                val relatedSchedules = todoRepository.loadSchedulesByTodoInfo(schedule.infoId)
                setState {
                    copy(
                        originSchedule = schedule,
                        memo = schedule.memo,
                        relatedScheduleCount = relatedSchedules.size,
                    )
                }
            } catch (e: Exception) {
                onShowSnackbar(getString(Res.string.snack_schedule_load_failed))
            }
        }
    }

    override suspend fun processIntent(intent: MemoIntent) {
        when (intent) {
            MemoIntent.OnBackClick -> onNavigateBack()
            is MemoIntent.OnMemoChange -> onMemoChange(intent.memo)
            MemoIntent.OnSaveClick -> onSaveClick()
            MemoIntent.OnDismissSaveDialog -> setState { copy(showSaveDialog = false) }
            MemoIntent.OnSaveToAllRelatedClick -> saveMemoToAllRelated()
            MemoIntent.OnSaveToSingleClick -> saveMemoToSingle()
        }
    }

    private fun onMemoChange(memo: String) {
        if (memo.length <= 100) {
            setState { copy(memo = memo) }
        }
    }

    private suspend fun onSaveClick() {
        if (!currentState.isSaveEnabled) return

        if (currentState.relatedScheduleCount <= 1) {
            saveMemoToSingle()
        } else {
            setState { copy(showSaveDialog = true) }
        }
    }

    private suspend fun saveMemoToSingle() {
        setState { copy(showSaveDialog = false) }
        val originSchedule = currentState.originSchedule ?: return

        try {
            val updatedSchedule = originSchedule.copy(memo = currentState.memo)
            todoRepository.updateTodo(updatedSchedule)
            val isEdit = originSchedule.memo.isNotEmpty()
            onShowSnackbar(
                if (isEdit) getString(Res.string.snack_memo_updated)
                else getString(Res.string.snack_memo_added)
            )
            onNavigateToHome(originSchedule.date)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_memo_save_failed))
        }
    }

    private suspend fun saveMemoToAllRelated() {
        setState { copy(showSaveDialog = false) }
        val originSchedule = currentState.originSchedule ?: return

        try {
            val relatedSchedules = todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId)
            todoRepository.updateTodos(relatedSchedules.map { it.copy(memo = currentState.memo) })

            val isEdit = originSchedule.memo.isNotEmpty()
            onShowSnackbar(
                if (isEdit) getString(Res.string.snack_memo_updated_all, relatedSchedules.size)
                else getString(Res.string.snack_memo_added_all, relatedSchedules.size)
            )
            onNavigateToHome(originSchedule.date)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_memo_save_failed))
        }
    }
}
