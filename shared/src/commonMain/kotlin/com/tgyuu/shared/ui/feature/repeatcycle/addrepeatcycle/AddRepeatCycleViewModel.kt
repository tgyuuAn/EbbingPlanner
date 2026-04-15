package com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.launch

class AddRepeatCycleViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
) : BaseViewModel<AddRepeatCycleState, AddRepeatCycleIntent>(AddRepeatCycleState()) {

    init {
        loadExperimentVariant()
    }

    override suspend fun processIntent(intent: AddRepeatCycleIntent) {
        when (intent) {
            AddRepeatCycleIntent.OnBackClick -> onNavigateBack()
            is AddRepeatCycleIntent.OnIntervalsChange -> onIntervalsChange(intent.intervals)
            AddRepeatCycleIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onIntervalsChange(intervals: String) {
        val allowed = intervals.matches(Regex("^[0-9,\\s]*$"))
        if (allowed) {
            setState { copy(intervals = intervals) }
        }
    }

    private suspend fun onSaveClick() {
        if (currentState.intervals.isEmpty()) {
            onShowSnackbar("필수 항목을 작성해주세요")
            return
        }

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                onShowSnackbar("반복 주기가 적절하지 않습니다.")
                return
            }

            try {
                todoRepository.addRepeatCycle(intervals = intervals)
                onShowSnackbar("반복 주기를 추가하였습니다")
                onNavigateBack()
            } catch (e: Exception) {
                onShowSnackbar("반복 주기 추가에 실패했습니다")
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
