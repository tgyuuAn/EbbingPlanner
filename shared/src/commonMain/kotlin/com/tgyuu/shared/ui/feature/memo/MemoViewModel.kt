package com.tgyuu.shared.ui.feature.memo

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_memo_added
import ebbingplanner.shared.generated.resources.snack_memo_save_failed
import ebbingplanner.shared.generated.resources.snack_memo_updated
import ebbingplanner.shared.generated.resources.snack_schedule_load_failed
import org.jetbrains.compose.resources.getString

class MemoViewModel(
    private val scheduleId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
) : BaseViewModel<MemoState, MemoIntent>(MemoState()) {

    init {
        loadExperimentVariant()
        loadSchedule()
    }

    private fun loadSchedule() {
        safeScope.launch {
            try {
                val schedule = todoRepository.loadSchedule(scheduleId) ?: run {
                    onNavigateBack()
                    return@launch
                }
                setState {
                    copy(
                        originSchedule = schedule,
                        memo = schedule.memo,
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
        }
    }

    private fun onMemoChange(memo: String) {
        if (memo.length <= 100) {
            setState { copy(memo = memo) }
        }
    }

    private suspend fun onSaveClick() {
        val originSchedule = currentState.originSchedule ?: return
        if (!currentState.isSaveEnabled) return

        try {
            val updatedSchedule = originSchedule.copy(memo = currentState.memo)
            todoRepository.updateTodo(updatedSchedule)
            val isEdit = originSchedule.memo.isNotEmpty()
            onShowSnackbar(if (isEdit) getString(Res.string.snack_memo_updated) else getString(Res.string.snack_memo_added))
            onNavigateToHome(originSchedule.date)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_memo_save_failed))
        }
    }

    private fun loadExperimentVariant() {
        safeScope.launch {
            val variant = experimentRepository?.getVariant(Experiment.SaveButtonPosition)
                ?: Experiment.SaveButtonPosition.Variant.CONTROL
            setState { copy(saveButtonPositionVariant = variant) }
        }
    }
}
