package com.tgyuu.ebbingplanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgyuu.domain.model.DefaultTodoTag
import com.tgyuu.domain.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
) : ViewModel() {
    init {
        viewModelScope.launch {
            todoRepository.addTag(DefaultTodoTag)
        }
    }
}
