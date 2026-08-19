package com.tgyuu.shared.ui.feature.tag

import androidx.lifecycle.viewModelScope
import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.logClick
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

class TagViewModel(
    private val todoRepository: TodoRepository,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToAddTag: () -> Unit,
    private val onNavigateToEditTag: (Int) -> Unit,
    private val analyticsHelper: AnalyticsHelper? = null,
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
        setState {
            copy(tagList = tagList.filterNot { it.id == tag.id }.toImmutableList())
        }
    }
}

private fun TodoTag.toUiModel() = TodoTagUiModel(
    id = id,
    name = name,
    color = color,
    createdAt = createdAt,
)
