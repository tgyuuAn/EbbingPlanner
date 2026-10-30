package com.tgyuu.shared

import com.tgyuu.shared.navigation.AppExternalEvents
import com.tgyuu.shared.navigation.WidgetDeepLink

/** Swift onOpenURL에서 호출: 위젯 딥링크를 RootContent로 전달해요 */
fun handleDeepLink(url: String) {
    WidgetDeepLink.parse(url)?.let(AppExternalEvents::emitDeepLink)
}

/** Swift scenePhase == .active 에서 호출: 외부(위젯)에서 바뀐 데이터를 다시 불러와요 */
fun handleAppDidBecomeActive() {
    AppExternalEvents.emitForeground()
}
