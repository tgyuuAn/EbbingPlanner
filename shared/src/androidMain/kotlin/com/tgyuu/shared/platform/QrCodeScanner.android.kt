package com.tgyuu.shared.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Android stub 구현 (컴파일 전용).
 *
 * shared 모듈의 Android 타깃은 실사용되지 않으며,
 * 실사용 Android 앱은 feature/sync 모듈의 CameraX + MLKit 구현을 사용한다.
 */
@Composable
actual fun QrCodeCameraPreview(
    onQrDetected: (String) -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier)
}
