package com.tgyuu.shared.platform

/**
 * 카메라 권한 상태 - expect/actual 패턴
 *
 * iOS: AVCaptureDevice.authorizationStatusForMediaType 기반
 * Android(shared): 실사용하지 않으므로 stub (실사용 Android 앱은 feature/sync 모듈 사용)
 */
enum class CameraPermissionStatus {
    GRANTED,
    NOT_DETERMINED,
    DENIED,
}

/** 현재 카메라 권한 상태 조회 */
expect fun getCameraPermissionStatus(): CameraPermissionStatus

/** 카메라 권한 요청. 결과 콜백은 메인 스레드에서 호출된다. */
expect fun requestCameraPermission(onResult: (Boolean) -> Unit)

/** 앱 설정 화면 열기 (권한 영구 거부 시 안내용) */
expect fun openAppSettings()
