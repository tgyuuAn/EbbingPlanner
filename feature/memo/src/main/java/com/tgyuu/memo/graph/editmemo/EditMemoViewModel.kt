package com.tgyuu.memo.graph.editmemo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.memo.graph.editmemo.contract.EditMemoIntent
import com.tgyuu.memo.graph.editmemo.contract.EditMemoState
import com.tgyuu.navigation.HomeGraph.HomeRoute
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMemoViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val eventBus: EventBus,
    private val analyticsHelper: AnalyticsHelper,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<EditMemoState, EditMemoIntent>(EditMemoState()) {

    init {
        val scheduleId = savedStateHandle.get<Int>("scheduleId")
            ?: throw IllegalArgumentException("해당 일정은 없습니다")

        viewModelScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId)
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
        if (!currentState.isSaveEnabled) {
            eventBus.sendEvent(EbbingEvent.ShowSnackBar("필수 항목을 작성해주세요"))
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
        eventBus.sendEvent(EbbingEvent.ShowSnackBar("메모를 수정하였습니다"))
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

        eventBus.sendEvent(EbbingEvent.ShowSnackBar("현재 일정 포함 ${relatedSchedules.size}개에 메모를 수정하였습니다"))
        navigationBus.navigate(
            NavigationEvent.To(
                route = HomeRoute(originSchedule.date.toFormattedString()),
                popUpTo = true,
            )
        )
    }
}
