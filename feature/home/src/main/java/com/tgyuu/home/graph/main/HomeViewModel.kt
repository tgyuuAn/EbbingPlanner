package com.tgyuu.home.graph.main

import androidx.lifecycle.SavedStateHandle
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.toFormattedString
import com.tgyuu.domain.model.TodoSchedule
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.home.graph.main.contract.HomeIntent
import com.tgyuu.home.graph.main.contract.HomeState
import com.tgyuu.navigation.HomeGraph
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel<HomeState, HomeIntent>(HomeState()) {

    internal suspend fun loadSchedules() {
        val schedules = todoRepository.loadSchedules()
        val todosByDate: Map<LocalDate, List<TodoSchedule>> = schedules.groupBy { it.date }
        val todosByInfo: Map<Int, List<TodoSchedule>> = schedules.groupBy { it.infoId }

        setState {
            copy(
                isLoading = false,
                schedulesByDateMap = todosByDate,
                schedulesByTodoInfo = todosByInfo
            )
        }
    }

    override suspend fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.OnAddTodoClick -> navigationBus.navigate(
                NavigationEvent.To(HomeGraph.AddTodoRoute(intent.selectedDate.toFormattedString()))
            )

            is HomeIntent.OnCheckedChange -> onCheckedChange(intent.todoSchedule)
        }
    }

    private suspend fun onCheckedChange(schedule: TodoSchedule) {
        val newSchedule = schedule.copy(isDone = !schedule.isDone)
        todoRepository.updateTodo(newSchedule)

        setState {
            val updatedByInfo = schedulesByTodoInfo
                .mapValues { (infoId, list) ->
                    if (infoId == schedule.infoId) {
                        list.map { if (it.id == schedule.id) newSchedule else it }
                    } else list
                }

            val updatedByDate = schedulesByDateMap
                .mapValues { (_, list) ->
                    list.map { if (it.id == schedule.id) newSchedule else it }
                }

            copy(
                schedulesByTodoInfo = updatedByInfo,
                schedulesByDateMap = updatedByDate
            )
        }
    }
}
