package com.tgyuu.tag.graph.main

import androidx.lifecycle.viewModelScope
import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.common.event.EventBus
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.tag.graph.main.contract.TagIntent
import com.tgyuu.tag.graph.main.contract.TagState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val eventBus: EventBus,
    private val navigationBus: NavigationBus,
) : BaseViewModel<TagState, TagIntent>(TagState()) {

    override suspend fun processIntent(intent: TagIntent) {
        when (intent) {
            TagIntent.OnBackClick -> navigationBus.navigate(NavigationEvent.Up)
        }
    }

    internal fun loadTags() = viewModelScope.launch {
        val tagList = todoRepository.loadTagList()
        setState { copy(tagList = tagList) }
    }
}
