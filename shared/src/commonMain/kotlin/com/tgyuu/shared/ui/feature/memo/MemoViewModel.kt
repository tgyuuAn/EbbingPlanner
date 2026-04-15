package com.tgyuu.shared.ui.feature.memo

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

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
                val schedule = todoRepository.loadSchedule(scheduleId)
                setState {
                    copy(
                        originSchedule = schedule,
                        memo = schedule.memo,
                    )
                }
            } catch (e: Exception) {
                onShowSnackbar("일정을 불러오는데 실패했습니다")
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
            onShowSnackbar("메모를 추가하였습니다")
            onNavigateToHome(originSchedule.date)
        } catch (e: Exception) {
            onShowSnackbar("메모 저장에 실패했습니다")
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
