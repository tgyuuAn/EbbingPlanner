package com.tgyuu.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiIntent>(
    initialState: S,
) : ViewModel() {
    private val _state = MutableStateFlow<S>(initialState)
    val state = _state.asStateFlow()

    protected val currentState: S get() = _state.value

    private val _events: Channel<E> = Channel(BUFFERED)
    private val _reducer = Channel<S.() -> S>(BUFFERED)

    init {
        _events.receiveAsFlow()
            .onEach(::processIntent)
            .launchIn(viewModelScope)

        _reducer.receiveAsFlow()
            .onEach { reduce -> _state.value = currentState.reduce() }
            .launchIn(viewModelScope)
    }

    fun onIntent(event: E) = viewModelScope.launch { _events.send(event) }

    protected abstract suspend fun processIntent(event: E)

    protected fun setState(reduce: S.() -> S) = viewModelScope.launch {
        _reducer.send(reduce)
    }
}
