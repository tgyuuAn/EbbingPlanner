package com.tgyuu.shared.ui.feature.repeatcycle.editrepeatcycle

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.Experiment
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.feature.repeatcycle.addrepeatcycle.parsingIntervals
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_repeat_invalid
import ebbingplanner.shared.generated.resources.snack_repeat_load_failed
import ebbingplanner.shared.generated.resources.snack_repeat_update_failed
import ebbingplanner.shared.generated.resources.snack_repeat_updated
import org.jetbrains.compose.resources.getString

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
                onShowSnackbar(getString(Res.string.snack_repeat_load_failed))
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
                onShowSnackbar(getString(Res.string.snack_repeat_invalid))
                return
            }

            try {
                val updatedRepeatCycle = RepeatCycle(
                    id = origin.id,
                    intervals = intervals,
                )
                todoRepository.updateRepeatCycle(updatedRepeatCycle)
                onShowSnackbar(getString(Res.string.snack_repeat_updated))
                onNavigateBack()
            } catch (e: Exception) {
                onShowSnackbar(getString(Res.string.snack_repeat_update_failed))
            }
        }.onFailure {
            onShowSnackbar(getString(Res.string.snack_repeat_invalid))
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
