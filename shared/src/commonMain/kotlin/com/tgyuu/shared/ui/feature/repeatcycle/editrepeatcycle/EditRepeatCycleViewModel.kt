package com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.parsingIntervals
import kotlinx.coroutines.launch

class EditRepeatCycleViewModel(
    private val repeatCycleId: Int,
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
) : BaseViewModel<EditRepeatCycleState, EditRepeatCycleIntent>(EditRepeatCycleState()) {

    init {
        loadExperimentVariant()
        loadRepeatCycle()
    }

    private fun loadRepeatCycle() {
        safeScope.launch {
            try {
                val repeatCycle = todoRepository.loadRepeatCycle(repeatCycleId)
                setState {
                    copy(
                        originRepeatCycle = repeatCycle,
                        intervals = repeatCycle.intervals.joinToString(", "),
                    )
                }
            } catch (e: Exception) {
                onShowSnackbar("반복 주기를 불러오는데 실패했습니다")
            }
        }
    }

    override suspend fun processIntent(intent: EditRepeatCycleIntent) {
        when (intent) {
            EditRepeatCycleIntent.OnBackClick -> onNavigateBack()
            is EditRepeatCycleIntent.OnIntervalsChange -> onIntervalsChange(intent.intervals)
            EditRepeatCycleIntent.OnUpdateClick -> onUpdateClick()
        }
    }

    private fun onIntervalsChange(intervals: String) {
        val allowed = intervals.matches(Regex("^[0-9,\\s]*$"))
        if (allowed) {
            setState { copy(intervals = intervals) }
        }
    }

    private suspend fun onUpdateClick() {
        val origin = currentState.originRepeatCycle ?: return
        if (!currentState.isSaveEnabled) return

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                onShowSnackbar("반복 주기가 적절하지 않습니다.")
                return
            }

            try {
                val updatedRepeatCycle = RepeatCycle(
                    id = origin.id,
                    intervals = intervals,
                )
                todoRepository.updateRepeatCycle(updatedRepeatCycle)
                onShowSnackbar("반복 주기를 수정하였습니다")
                onNavigateBack()
            } catch (e: Exception) {
                onShowSnackbar("반복 주기 수정에 실패했습니다")
            }
        }.onFailure {
            onShowSnackbar("반복 주기가 적절하지 않습니다.")
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
