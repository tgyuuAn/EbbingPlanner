package com.tgyuu.repeatcycle.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.model.RepeatCycle
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.RepeatCycleGraph
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleIntent
import com.tgyuu.repeatcycle.graph.main.contract.RepeatCycleState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepeatCycleViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<RepeatCycleState, RepeatCycleIntent>(RepeatCycleState()) {

    override suspend fun processIntent(intent: RepeatCycleIntent) {
        when (intent) {
            RepeatCycleIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
            is RepeatCycleIntent.OnDeleteClick -> deleteRepeatCycle(intent.repeatCycle)
            is RepeatCycleIntent.OnEditClick -> navigationBus.navigate(
                NavigationEvent.To(RepeatCycleGraph.EditRepeatCycleRoute(intent.repeatCycle.id))
            )
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val repeatCycleList = todoRepository.loadRepeatCycles()
        setState { copy(repeatCycleList = repeatCycleList) }
    }

    private suspend fun deleteRepeatCycle(repeatCycle: RepeatCycle) {
        todoRepository.deleteRepeatCycle(repeatCycle)
        setState { copy(repeatCycleList = repeatCycleList.filterNot { it.id == repeatCycle.id }) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("반복 주기를 삭제하였습니다"))
    }
}
