package com.tgyuu.shared.platform

import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual fun getCameraPermissionStatus(): CameraPermissionStatus =
    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
        AVAuthorizationStatusAuthorized -> CameraPermissionStatus.GRANTED
        AVAuthorizationStatusNotDetermined -> CameraPermissionStatus.NOT_DETERMINED
        else -> CameraPermissionStatus.DENIED
    }

actual fun requestCameraPermission(onResult: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
        // 콜백은 임의의 큐에서 호출되므로 메인 큐로 전달
        dispatch_async(dispatch_get_main_queue()) {
            onResult(granted)
        }
    }
}

actual fun openAppSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url)
}
