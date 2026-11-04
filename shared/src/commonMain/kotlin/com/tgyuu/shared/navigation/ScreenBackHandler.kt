package com.tgyuu.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler

/** 현재 그려지는 스택 화면의 BackHandler (RootContent가 child마다 제공) */
val LocalScreenBackHandler = staticCompositionLocalOf<BackHandler?> { null }

/**
 * Android androidx.activity.compose.BackHandler 대응.
 * enabled일 때 시스템 뒤로가기(iOS 엣지 스와이프)를 가로채 onBack을 실행하고, 스택 pop은 하지 않아요.
 * child BackHandler의 콜백은 스택의 기본 pop 콜백보다 먼저 호출돼요.
 */
@Composable
fun ScreenBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit,
) {
    val backHandler = LocalScreenBackHandler.current ?: return
    val currentOnBack by rememberUpdatedState(onBack)
    val callback = remember { BackCallback(isEnabled = enabled) { currentOnBack() } }

    SideEffect { callback.isEnabled = enabled }

    DisposableEffect(backHandler, callback) {
        backHandler.register(callback)
        onDispose { backHandler.unregister(callback) }
    }
}
