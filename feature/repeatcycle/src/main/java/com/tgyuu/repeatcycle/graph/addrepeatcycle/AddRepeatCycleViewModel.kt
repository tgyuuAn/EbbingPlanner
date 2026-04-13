package com.tgyuu.memo.graph.addmemo

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
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleState
import com.tgyuu.repeatcycle.util.parsingIntervals
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AddRepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val experimentRepository: ExperimentRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel<AddRepeatCycleState, AddRepeatCycleIntent>(AddRepeatCycleState(saveButtonPositionVariant = runBlocking { experimentRepository.getVariant(Experiment.SaveButtonPosition) })) {

    override suspend fun processIntent(intent: AddRepeatCycleIntent) {
        when (intent) {
            is AddRepeatCycleIntent.OnRepeatCycleChange -> onRepeatCycleChange(intent.repeatCycle)
            AddRepeatCycleIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "AddRepeatCycle", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            AddRepeatCycleIntent.OnSaveClick -> saveRepeatCycle()
        }
    }

    private fun onRepeatCycleChange(repeatCycle: String) {
        val allowed = repeatCycle.matches(Regex("^[0-9,\\s]*$"))

        if (allowed) { setState { copy(intervals = repeatCycle) } }
    }

    private suspend fun saveRepeatCycle() {
        if (currentState.intervals.isEmpty()) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
            return
        }

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기가 적절하지 않습니다."))
                return
            }

            analyticsHelper.logEvent(
                AnalyticsEvent.Click(
                    screenName = "AddRepeatCycle",
                    buttonName = "SaveRepeatCycle",
                    properties = mapOf("variant" to currentState.saveButtonPositionVariant.key)
                )
            )
            todoRepository.addRepeatCycle(intervals = intervals)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기를 추가하였습니다"))
            navigationBus.navigate(NavigationEvent.Up)
        }.onFailure {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기가 적절하지 않습니다."))
            return
        }
    }
}
