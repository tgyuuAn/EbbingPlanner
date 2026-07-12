package com.tgyuu.shared.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 인라인 카메라 QR 스캔 프리뷰 - expect/actual 패턴
 *
 * iOS: AVCaptureSession + AVCaptureMetadataOutput(QR) + AVCaptureVideoPreviewLayer
 * Android(shared): stub (빈 화면). 실사용 Android 앱은 feature/sync 모듈의
 * CameraX + MLKit 구현을 사용한다.
 *
 * 카메라 권한이 허용된 상태에서 호출해야 한다.
 */
@Composable
expect fun QrCodeCameraPreview(
    onQrDetected: (String) -> Unit,
    modifier: Modifier = Modifier,
)
