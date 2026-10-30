package com.tgyuu.shared.ui.feature.tag

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.snack_tag_deleted
import org.jetbrains.compose.resources.getString

class TagViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToAddTag: () -> Unit,
    private val onNavigateToEditTag: (Int) -> Unit,
    private val analyticsHelper: AnalyticsHelper? = null,
    private val alarmRescheduler: AlarmRescheduler? = null,
    private val onShowSnackbar: (String) -> Unit = {},
) : BaseViewModel<TagState, TagIntent>(TagState()) {

    init {
        loadTags()
    }

    override suspend fun processIntent(intent: TagIntent) {
        when (intent) {
            TagIntent.OnBackClick -> {
                analyticsHelper.logClick("Tag", "Back")
                onNavigateBack()
            }
            TagIntent.OnAddClick -> {
                analyticsHelper.logClick("Tag", "AddTag")
                onNavigateToAddTag()
            }
            is TagIntent.OnEditClick -> {
                analyticsHelper.logClick("Tag", "EditTag")
                onNavigateToEditTag(intent.tag.id)
            }
            is TagIntent.OnDeleteClick -> {
                analyticsHelper.logClick("Tag", "DeleteTag")
                deleteTag(intent.tag)
            }
        }
    }

    private fun loadTags() = viewModelScope.launch {
        setState { copy(isLoading = true) }
        val tagList = todoRepository.loadTags()
        setState {
            copy(
                tagList = tagList.map { it.toUiModel() }.toImmutableList(),
                isLoading = false,
            )
        }
    }

    private suspend fun deleteTag(tag: TodoTagUiModel) {
        val domainTag = TodoTag(
            id = tag.id,
            name = tag.name,
            color = tag.color,
            createdAt = tag.createdAt,
        )
        todoRepository.deleteTag(domainTag)
        // 태그 삭제로 해당 태그 일정이 목록에서 빠지므로 알림을 다시 계산
        alarmRescheduler?.rescheduleAll()
        setState {
            copy(tagList = tagList.filterNot { it.id == tag.id }.toImmutableList())
        }
        // Android TagViewModel.deleteTag와 동일: 삭제 완료 안내
        onShowSnackbar(getString(Res.string.snack_tag_deleted))
    }
}

private fun TodoTag.toUiModel() = TodoTagUiModel(
    id = id,
    name = name,
    color = color,
    createdAt = createdAt,
)
