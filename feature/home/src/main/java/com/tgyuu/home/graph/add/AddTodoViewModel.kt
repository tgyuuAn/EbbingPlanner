package com.tgyuu.home.graph.add

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.home.graph.add.contract.AddTodoIntent
import com.tgyuu.home.graph.add.contract.AddTodoState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor() :
    BaseViewModel<AddTodoState, AddTodoIntent>(AddTodoState()) {
    override suspend fun processIntent(event: AddTodoIntent) {
        TODO("Not yet implemented")
    }
}
