package com.tgyuu.memo.graph.editmemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.experiment.domain.model.Experiment
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.experiment.domain.repository.ExperimentRepository
import com.tgyuu.memo.graph.editmemo.contract.EditMemoIntent
import com.tgyuu.memo.graph.editmemo.contract.EditMemoState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class EditMemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val experimentRepository: ExperimentRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditMemoState, EditMemoIntent>(EditMemoState(saveButtonPositionVariant = runBlocking { experimentRepository.getVariant(Experiment.SaveButtonPosition) })) {

    init {
        analyticsHelper.logEvent(
            AnalyticsEvent.View(
                screenName = "EditMemo",
                properties = mapOf("variant" to currentState.saveButtonPositionVariant.key + "_V2"),
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
                    memo = originSchedule.memo,
                    relatedScheduleCount = relatedSchedules.size,
                )
            }
        }
    }

    override suspend fun processIntent(intent: EditMemoIntent) {
        when (intent) {
            is EditMemoIntent.OnMemoChange -> onMemoChange(intent.memo)
            EditMemoIntent.OnBackClick -> {
                analyticsHelper.logEvent(
                    AnalyticsEvent.Click(screenName = "EditMemo", buttonName = "Back")
                )
                navigationBus.navigate(NavigationEvent.Up)
            }
            EditMemoIntent.OnUpdateClick -> onUpdateClick()
            EditMemoIntent.OnDismissSaveDialog -> dismissSaveDialog()
            EditMemoIntent.OnSaveToAllRelatedClick -> saveMemoToAllRelated()
            EditMemoIntent.OnSaveToSingleClick -> saveMemoToSingle()
        }
    }

    private fun onMemoChange(memo: String) {
        setState { copy(memo = memo) }
    }

    private suspend fun onUpdateClick() {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(
                screenName = "EditMemo",
                buttonName = "Save",
                properties = mapOf("variant" to currentState.saveButtonPositionVariant.key + "_V2")
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
        todoRepository.updateTodo(schedule.copy(memo = currentState.memo))
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.memo_updated)))
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
        val relatedSchedules = todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId)

        todoRepository.updateTodos(relatedSchedules.map { it.copy(memo = currentState.memo) })

        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.memo_updated_all, relatedSchedules.size)))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(originSchedule.date.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
