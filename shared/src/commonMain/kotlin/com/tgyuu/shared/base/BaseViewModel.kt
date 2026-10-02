package com.tgyuu.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseViewModel<S : UiState, E : UiIntent>(
    initialState: S,
) : ViewModel() {
    private val _state = MutableStateFlow<S>(initialState)
    val state = _state.asStateFlow()

    protected val currentState: S get() = _state.value

    private val _events: Channel<E> = Channel(BUFFERED)
    private val _reducer = Channel<S.() -> S>(BUFFERED)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("BaseViewModel error: ${throwable.message}")
        throwable.printStackTrace()
    }

    protected val safeScope get() = viewModelScope + exceptionHandler

    init {
        _events.receiveAsFlow()
            .onEach { intent ->
                try {
                    processIntent(intent)
                } catch (e: Exception) {
                    println("processIntent error: ${e.message}")
                }
            }
            .catch { e -> println("Event flow error: ${e.message}") }
            .launchIn(safeScope)

        _reducer.receiveAsFlow()
            .onEach { reduce ->
                try {
                    _state.value = currentState.reduce()
                } catch (e: Exception) {
                    println("Reducer error: ${e.message}")
                }
            }
            .catch { e -> println("Reducer flow error: ${e.message}") }
            .launchIn(safeScope)
    }

    fun onIntent(intent: E) = safeScope.launch { _events.send(intent) }

    protected abstract suspend fun processIntent(intent: E)

    protected fun setState(reduce: S.() -> S) = safeScope.launch {
        _reducer.send(reduce)
    }
}
