package com.tgyuu.memo.graph.addmemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.memo.graph.addmemo.contract.AddMemoIntent
import com.tgyuu.memo.graph.addmemo.contract.AddMemoState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import kotlinx.coroutines.launch

class AddMemoViewModel(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<AddMemoState, AddMemoIntent>(AddMemoState()) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "AddMemo",
            )
        )

        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId) ?: run {
                navigationBus.navigate(NavigationEvent.Up)
                return@launch
            }
            val relatedSchedules = todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId)

            setState {
                copy(
                    originSchedule = originSchedule,
                    relatedScheduleCount = relatedSchedules.size,
                )
            }
        }
    }

    override suspend fun processIntent(intent: AddMemoIntent) {
        when (intent) {
            is AddMemoIntent.OnMemoChange -> onMemoChange(intent.memo)
            AddMemoIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "AddMemo", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            AddMemoIntent.OnSaveClick -> onSaveClick()
            AddMemoIntent.OnDismissSaveDialog -> dismissSaveDialog()
            AddMemoIntent.OnSaveToAllRelatedClick -> saveMemoToAllRelated()
            AddMemoIntent.OnSaveToSingleClick -> saveMemoToSingle()
        }
    }

    private fun onMemoChange(memo: String) {
        setState { copy(memo = memo) }
    }

    private suspend fun onSaveClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "AddMemo",
                buttonName = "Save",
            )
        )

        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.memo_required_fields)))
            return
        }

        if (currentState.relatedScheduleCount <= 1) {
            saveMemoToSingle()
        } else {
            setState { copy(showSaveDialog = true) }
        }
    }

    private fun dismissSaveDialog() {
        setState { copy(showSaveDialog = false) }
    }

    private suspend fun saveMemoToSingle() {
        setState { copy(showSaveDialog = false) }
        val schedule = currentState.originSchedule ?: return
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "AddMemo", buttonName = "SaveMemoSingle")
        )
        todoRepository.updateTodo(schedule.copy(memo = currentState.memo))
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.memo_added)))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(schedule.date.toFormattedString()),
                popUpTo = true,
            )
        )
    }

    private suspend fun saveMemoToAllRelated() {
        setState { copy(showSaveDialog = false) }
        val originSchedule = currentState.originSchedule ?: return
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = "AddMemo", buttonName = "SaveMemoAll")
        )
        val relatedSchedules = todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId)

        todoRepository.updateTodos(relatedSchedules.map { it.copy(memo = currentState.memo) })

        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.memo_added_all, relatedSchedules.size)))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(originSchedule.date.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
