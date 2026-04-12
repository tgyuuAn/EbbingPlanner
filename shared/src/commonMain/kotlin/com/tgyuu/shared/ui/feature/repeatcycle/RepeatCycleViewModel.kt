package com.tgyuu.shared.ui.feature.repeatcycle

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class RepeatCycleViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToAddRepeatCycle: () -> Unit,
    private val onNavigateToEditRepeatCycle: (Int) -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<RepeatCycleState, RepeatCycleIntent>(RepeatCycleState()) {

    init {
        loadRepeatCycles()
    }

    override suspend fun processIntent(intent: RepeatCycleIntent) {
        when (intent) {
            RepeatCycleIntent.OnBackClick -> onNavigateBack()
            RepeatCycleIntent.OnAddClick -> onNavigateToAddRepeatCycle()
            is RepeatCycleIntent.OnEditClick -> onNavigateToEditRepeatCycle(intent.repeatCycle.id)
            is RepeatCycleIntent.OnDeleteClick -> deleteRepeatCycle(intent.repeatCycle)
        }
    }

    private fun loadRepeatCycles() = viewModelScope.launch {
        setState { copy(isLoading = true) }
        try {
            val repeatCycleList = todoRepository.loadRepeatCycles()
            setState {
                copy(
                    repeatCycleList = repeatCycleList.map { it.toUiModel() }.toImmutableList(),
                    isLoading = false,
                )
            }
        } catch (e: Exception) {
            setState { copy(isLoading = false) }
            onShowSnackbar("반복 주기를 불러오는데 실패했습니다")
        }
    }

    private suspend fun deleteRepeatCycle(repeatCycle: RepeatCycleUiModel) {
        try {
            val domainRepeatCycle = RepeatCycle(
                id = repeatCycle.id,
                intervals = repeatCycle.intervals.toList(),
            )
            todoRepository.deleteRepeatCycle(domainRepeatCycle)
            setState {
                copy(repeatCycleList = repeatCycleList.filterNot { it.id == repeatCycle.id }.toImmutableList())
            }
            onShowSnackbar("반복 주기를 삭제했습니다")
        } catch (e: Exception) {
            onShowSnackbar("반복 주기 삭제에 실패했습니다")
        }
    }

    private fun RepeatCycle.toUiModel() = RepeatCycleUiModel(
        id = id,
        intervals = intervals.toImmutableList(),
        displayName = toDisplayName(),
    )

    private fun RepeatCycle.toDisplayName(): String {
        if (intervals.isEmpty()) return "올바른 형태로 작성해주세요."

        return when {
            intervals.size == 1 && intervals.first() == 0 -> "당일만"
            else -> intervals.joinToString(", ") { day ->
                if (day == 0) "당일" else "${day}일"
            }
        }
    }
}
