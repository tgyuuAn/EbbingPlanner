package com.tgyuu.repeatcycle.graph.editrepeatcycle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.editrepeatcycle.contract.EditRepeatCycleState
import com.tgyuu.repeatcycle.util.parsingIntervals
import kotlinx.coroutines.launch

class EditRepeatCycleViewModel(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<EditRepeatCycleState, EditRepeatCycleIntent>(EditRepeatCycleState(resourceProvider = resourceProvider)) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "EditRepeatCycle",
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
            )
        )

        if (currentState.intervals.isEmpty()) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_required)))
            return
        }

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_invalid)))
                return
            }

            val newRepeatCycle =
                currentState.originRepeatCycle?.copy(intervals = intervals) ?: return
            todoRepository.updateRepeatCycle(newRepeatCycle)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_updated)))
            navigationBus.navigate(NavigationEvent.Up)
        }.onFailure {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_invalid)))
            return
        }
    }
}
