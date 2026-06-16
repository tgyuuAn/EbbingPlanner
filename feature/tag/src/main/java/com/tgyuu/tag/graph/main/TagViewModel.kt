package com.tgyuu.tag.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsEvent
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EbbingEvent
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.model.TodoTagUiModel
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.TagGraph
import com.tgyuu.tag.graph.main.contract.TagIntent
import com.tgyuu.tag.graph.main.contract.TagState
import com.tgyuu.tag.model.toDomainModel
import com.tgyuu.tag.model.toUiModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
    private val analyticsHelper: AnalyticsHelper,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<TagState, TagIntent>(TagState()) {

    override suspend fun processIntent(intent: TagIntent) {
        when (intent) {
            TagIntent.OnBackClick -> onBackClick()
            TagIntent.OnAddClick -> onAddClick()
            is TagIntent.OnDeleteClick -> onDeleteClick(intent.tag)
            is TagIntent.OnEditClick -> onEditClick(intent.tag)
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
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "AddTag")
        )
        navigationBus.navigate(
            NavigationEvent.To(TagGraph.AddTagRoute)
        )
    }

    private suspend fun onDeleteClick(tag: TodoTagUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "DeleteTag")
        )
        deleteTag(tag)
    }

    private suspend fun onEditClick(tag: TodoTagUiModel) {
        analyticsHelper.logEvent(
            AnalyticsEvent.Click(screenName = SCREEN_NAME, buttonName = "EditTag")
        )
        navigationBus.navigate(
            NavigationEvent.To(TagGraph.EditTagRoute(tag.id))
        )
    }

    companion object {
        private const val SCREEN_NAME = "Tag"
    }

    internal fun loadTags() = viewModelScope.launch {
        val tagList = todoRepository.loadTags()
        setState { copy(tagList = tagList.toUiModels()) }
    }

    private suspend fun deleteTag(tag: TodoTagUiModel) {
        todoRepository.deleteTag(tag.toDomainModel())
        setState { copy(tagList = tagList.filterNot { it.id == tag.id }.toImmutableList()) }
        eventBus.sendEvent(EbbingEvent.ShowSnackBar(resourceProvider.getString(R.string.tag_deleted)))
    }
}
