package com.tgyuu.shared.platform

/**
 * shared 의 Android 타깃은 출시 앱이 아니다(출시 앱은 feature/sync NetworkMonitor 사용).
 * 컴파일·테스트용으로 항상 연결된 상태를 돌려준다.
 */
actual fun createNetworkMonitor(): NetworkMonitor = AlwaysConnectedNetworkMonitor()
