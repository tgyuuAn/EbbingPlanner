package com.tgyuu.memo.graph.addmemo

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
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleIntent
import com.tgyuu.repeatcycle.graph.addrepeatcycle.contract.AddRepeatCycleState
import com.tgyuu.repeatcycle.util.parsingIntervals
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AddRepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<AddRepeatCycleState, AddRepeatCycleIntent>(AddRepeatCycleState(resourceProvider = resourceProvider)) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "AddRepeatCycle",
            )
        )
    }

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
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_required)))
            return
        }

        parsingIntervals(currentState.intervals).onSuccess { intervals ->
            if (intervals.isEmpty()) {
                eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_invalid)))
                return
            }

            analyticsHelper.logEvent(
                AnalyticsEvent.Click(
                    screenName = "AddRepeatCycle",
                    buttonName = "SaveRepeatCycle",
                )
            )
            todoRepository.addRepeatCycle(intervals = intervals)
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_added)))
            navigationBus.navigate(NavigationEvent.Up)
        }.onFailure {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_invalid)))
            return
        }
    }
}
