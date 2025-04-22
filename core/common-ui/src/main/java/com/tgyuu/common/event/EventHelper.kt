package com.tgyuu.common.event

import androidx.compose.runtime.Composable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

typealias BottomSheetContent = @Composable (() -> Unit)

@Singleton
class EventHelper @Inject constructor() {
    private val _eventFlow = Channel<EbbingEvent>(BUFFERED)
    val eventFlow = _eventFlow.receiveAsFlow()

    suspend fun sendEvent(event: EbbingEvent) {
        _eventFlow.send(event)
    }
}

sealed class EbbingEvent {
    data class ShowBottomSheet(val content: BottomSheetContent) : EbbingEvent()
    data object HideBottomSheet : EbbingEvent()
}
