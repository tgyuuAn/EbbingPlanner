package com.tgyuu.repeatcycle.graph.editrepeatcycle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.experiment.domain.repository.ExperimentRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleState
import com.tgyuu.repeatcycle.util.parsingIntervals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditRepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val experimentRepository: ExperimentRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditRepeatCycleState, EditRepeatCycleIntent>(EditRepeatCycleState(saveButtonPositionVariant = runBlocking { experimentRepository.getVariant(Experiment.SaveButtonPosition) })) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "EditRepeatCycle",
                properties = mapOf("variant" to currentState.saveButtonPositionVariant.key + "_V2"),
            )
        )

        val repeatCycleId = savedStateHandle.get<Int>("repeatCycleId")
            ?: throw IllegalArgumentException("해당 반복 주기는 없습니다")

        viewModelScope.launch {
            val originRepeatCycle = todoRepository.loadRepeatCycle(repeatCycleId)

            setState {
                copy(
                    originRepeatCycle = originRepeatCycle,
                    intervals = originRepeatCycle.intervals.joinToString(", ")
                )
            }
        }
    }

    override suspend fun processIntent(intent: EditRepeatCycleIntent) {
        when (intent) {
            is EditRepeatCycleIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            EditRepeatCycleIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "EditRepeatCycle", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            EditRepeatCycleIntent.OnUpdateClick -> updateRepeatCycle()
        }
    }

    private fun onRepeatCycleChange(repeatCycle: String) {
        val allowed = repeatCycle.matches(Regex("^[0-9,\\s]*$"))

        if (allowed) { setState { copy(intervals = repeatCycle) } }
    }

    private suspend fun updateRepeatCycle() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "EditRepeatCycle",
                buttonName = "Save",
                properties = mapOf("variant" to currentState.saveButtonPositionVariant.key + "_V2")
            )
        )

        if (currentState.intervals.isEmpty()) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기가 적절하지 않습니다."))
                return
            }

            val newRepeatCycle =
                currentState.originRepeatCycle?.copy(intervals = intervals) ?: return
            todoRepository.updateRepeatCycle(newRepeatCycle)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기를 수정하였습니다"))
            navigationBus.navigate(NavigationEvent.Up)
        }.onFailure {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기가 적절하지 않습니다."))
            return
        }
    }
}
