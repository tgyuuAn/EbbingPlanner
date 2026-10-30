package com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_repeat_add_failed
import ebbingplanner.shared.generated.resources.repeat_snackbar_added
import ebbingplanner.shared.generated.resources.repeat_snackbar_invalid
import ebbingplanner.shared.generated.resources.repeat_snackbar_required
import org.jetbrains.compose.resources.getString

class AddRepeatCycleViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val analyticsHelper: AnalyticsHelper? = null,
) : BaseViewModel<AddRepeatCycleState, AddRepeatCycleIntent>(AddRepeatCycleState()) {

    init {
        loadExperimentVariant()
    }

    override suspend fun processIntent(intent: AddRepeatCycleIntent) {
        when (intent) {
            AddRepeatCycleIntent.OnBackClick -> {
                analyticsHelper.logClick("AddRepeatCycle", "Back")
                onNavigateBack()
            }
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
            onShowSnackbar(getString(Res.string.repeat_snackbar_required))
            return
        }

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                onShowSnackbar(getString(Res.string.repeat_snackbar_invalid))
                return
            }

            // Android와 동일: 검증을 통과한 뒤에만 저장 클릭을 로깅해요
            analyticsHelper.logClick("AddRepeatCycle", "SaveRepeatCycle")

            try {
                todoRepository.addRepeatCycle(intervals = intervals)
                onShowSnackbar(getString(Res.string.repeat_snackbar_added))
                onNavigateBack()
            } catch (e: Exception) {
                onShowSnackbar(getString(Res.string.snack_repeat_add_failed))
            }
        }.onFailure {
            onShowSnackbar(getString(Res.string.repeat_snackbar_invalid))
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
