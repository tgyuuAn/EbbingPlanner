package com.tgyuu.repeatcycle.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.model.RepeatCycleUiModel
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleIntent
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleState
import com.tgyuu.repeatcycle.model.toDomainModel
import com.tgyuu.repeatcycle.model.toUiModels
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class RepeatCycleViewModel(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<RepeatCycleState, RepeatCycleIntent>(RepeatCycleState()) {

    override suspend fun processIntent(intent: RepeatCycleIntent) {
        when (intent) {
            RepeatCycleIntent.OnBackClick -> onBackClick()
            RepeatCycleIntent.OnAddClick -> onAddClick()
            is RepeatCycleIntent.OnDeleteClick -> onDeleteClick(intent.repeatCycle)
            is RepeatCycleIntent.OnEditClick -> onEditClick(intent.repeatCycle)
        }
    }

    private suspend fun onBackClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "Back")
        )
        navigationBus.navigate(NavigationEvent.Up)
    }

    private suspend fun onAddClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "AddRepeatCycle")
        )
        navigationBus.navigate(
            NavigationEvent.To(RepeatCycleGraph.AddRepeatCycleRoute)
        )
    }

    private suspend fun onDeleteClick(repeatCycle: RepeatCycleUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "DeleteRepeatCycle")
        )
        deleteRepeatCycle(repeatCycle)
    }

    private suspend fun onEditClick(repeatCycle: RepeatCycleUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "EditRepeatCycle")
        )
        navigationBus.navigate(
            NavigationEvent.To(RepeatCycleGraph.EditRepeatCycleRoute(repeatCycle.id))
        )
    }

    companion object {
        private const val SCREEN_NAME = "RepeatCycle"
    }

    internal fun loadTags() = viewModelScope.launch {
        val repeatCycleList = todoRepository.loadRepeatCycles()
        setState { copy(repeatCycleList = repeatCycleList.toUiModels(resourceProvider)) }
    }

    private suspend fun deleteRepeatCycle(repeatCycle: RepeatCycleUiModel) {
        todoRepository.deleteRepeatCycle(repeatCycle.toDomainModel())
        setState { copy(repeatCycleList = repeatCycleList.filterNot { it.id == repeatCycle.id }.toImmutableList()) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.repeat_snackbar_deleted)))
    }
}
