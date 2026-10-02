package com.tgyuu.shared.ui.feature.repeatcycle

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.ui.model.RepeatCycleUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_repeat_delete_failed
import ebbingplanner.shared.generated.resources.snack_repeat_deleted
import ebbingplanner.shared.generated.resources.snack_repeat_load_failed
import org.jetbrains.compose.resources.getString
import com.tgyuu.shared.designsystem.model.toDisplayName
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick

class RepeatCycleViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToAddRepeatCycle: () -> Unit,
    private val onNavigateToEditRepeatCycle: (Int) -> Unit,
    private val onShowSnackbar: (String) -> Unit = {},
    private val analyticsHelper: AnalyticsHelper? = null,
) : BaseViewModel<RepeatCycleState, RepeatCycleIntent>(RepeatCycleState()) {

    init {
        loadRepeatCycles()
    }

    override suspend fun processIntent(intent: RepeatCycleIntent) {
        when (intent) {
            RepeatCycleIntent.OnBackClick -> {
                analyticsHelper.logClick("RepeatCycle", "Back")
                onNavigateBack()
            }
            RepeatCycleIntent.OnAddClick -> {
                analyticsHelper.logClick("RepeatCycle", "AddRepeatCycle")
                onNavigateToAddRepeatCycle()
            }
            is RepeatCycleIntent.OnEditClick -> {
                analyticsHelper.logClick("RepeatCycle", "EditRepeatCycle")
                onNavigateToEditRepeatCycle(intent.repeatCycle.id)
            }
            is RepeatCycleIntent.OnDeleteClick -> {
                analyticsHelper.logClick("RepeatCycle", "DeleteRepeatCycle")
                deleteRepeatCycle(intent.repeatCycle)
            }
        }
    }

    private fun loadRepeatCycles() = viewModelScope.launch {
        setState { copy(isLoading = true) }
        try {
            val loaded = todoRepository.loadRepeatCycles()
            val models = buildList { for (cycle in loaded) add(cycle.toUiModel()) }
            setState {
                copy(
                    repeatCycleList = models.toImmutableList(),
                    isLoading = false,
                )
            }
        } catch (e: Exception) {
            setState { copy(isLoading = false) }
            onShowSnackbar(getString(Res.string.snack_repeat_load_failed))
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
            onShowSnackbar(getString(Res.string.snack_repeat_deleted))
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_repeat_delete_failed))
        }
    }

    private suspend fun RepeatCycle.toUiModel() = RepeatCycleUiModel(
        id = id,
        intervals = intervals.toImmutableList(),
        displayName = toDisplayName(),
    )
}
