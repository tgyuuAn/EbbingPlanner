package com.tgyuu.shared.platform

/**
 * Android stub 구현.
 *
 * shared 모듈의 Android 타깃은 실사용되지 않는다.
 * 실사용 Android 앱은 feature/sync 모듈(accompanist-permissions)을 사용한다.
 */
actual fun getCameraPermissionStatus(): CameraPermissionStatus = CameraPermissionStatus.GRANTED

actual fun requestCameraPermission(onResult: (Boolean) -> Unit) {
    onResult(true)
}

actual fun openAppSettings() {
    // no-op: shared Android 타깃은 실사용하지 않음
}
