package com.tgyuu.repeatcycle.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleIntent
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleState
import com.tgyuu.repeatcycle.model.toDomainModel
import com.tgyuu.repeatcycle.model.toUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val analyticsHelper: AnalyticsHelper,
) : BaseViewModel<RepeatCycleState, RepeatCycleIntent>(RepeatCycleState()) {

    override suspend fun processIntent(intent: RepeatCycleIntent) {
        when (intent) {
            RepeatCycleIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "RepeatCycle", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            is RepeatCycleIntent.OnDeleteClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "RepeatCycle", buttonName = "DeleteRepeatCycle")
                )
                deleteRepeatCycle(intent.repeatCycle)
            }
            is RepeatCycleIntent.OnEditClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "RepeatCycle", buttonName = "EditRepeatCycle")
                )
                navigationBus.navigate(
                    NavigationEvent.To(RepeatCycleGraph.EditRepeatCycleRoute(intent.repeatCycle.id))
                )
            }
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val repeatCycleList = todoRepository.loadRepeatCycles()
        setState { copy(repeatCycleList = repeatCycleList.toUiModels()) }
    }

    private suspend fun deleteRepeatCycle(repeatCycle: RepeatCycleUiModel) {
        todoRepository.deleteRepeatCycle(repeatCycle.toDomainModel())
        setState { copy(repeatCycleList = repeatCycleList.filterNot { it.id == repeatCycle.id }.toImmutableList()) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기를 삭제하였습니다"))
    }
}
