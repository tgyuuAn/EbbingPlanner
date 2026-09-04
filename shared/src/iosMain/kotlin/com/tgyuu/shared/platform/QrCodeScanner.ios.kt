package com.tgyuu.shared.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.CoreGraphics.CGRectMake
import platform.QuartzCore.CATransaction
import platform.UIKit.UIView
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

/**
 * AVFoundation 기반 QR 스캔 프리뷰.
 *
 * AVCaptureSession + AVCaptureMetadataOutput(QR 전용)으로 스캔하고,
 * AVCaptureVideoPreviewLayer를 UIKitView로 노출한다.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun QrCodeCameraPreview(
    onQrDetected: (String) -> Unit,
    modifier: Modifier,
) {
    val currentOnQrDetected by rememberUpdatedState(onQrDetected)
    val session = remember { AVCaptureSession() }
    val delegate = remember {
        QrScannerDelegate { rawValue -> currentOnQrDetected(rawValue) }
    }

    DisposableEffect(session) {
        onDispose {
            session.stopRunning()
        }
    }

    UIKitView(
        factory = {
            val previewLayer = AVCaptureVideoPreviewLayer(session = session)
            previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill

            val view = CameraPreviewView(previewLayer)
            view.layer.addSublayer(previewLayer)

            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device != null) {
                val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
                if (input != null && session.canAddInput(input)) {
                    session.addInput(input)
                }

                val metadataOutput = AVCaptureMetadataOutput()
                if (session.canAddOutput(metadataOutput)) {
                    session.addOutput(metadataOutput)
                    metadataOutput.setMetadataObjectsDelegate(delegate, dispatch_get_main_queue())
                    if (metadataOutput.availableMetadataObjectTypes.contains(AVMetadataObjectTypeQRCode)) {
                        metadataOutput.metadataObjectTypes = listOf(AVMetadataObjectTypeQRCode)
                    }
                }

                // startRunning은 블로킹 호출이므로 백그라운드 큐에서 시작
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0uL)) {
                    session.startRunning()
                }
            }

            view
        },
        modifier = modifier,
    )
}

/** 뷰 크기 변경 시 프리뷰 레이어 프레임을 동기화하는 UIView */
@OptIn(ExperimentalForeignApi::class)
private class CameraPreviewView(
    private val previewLayer: AVCaptureVideoPreviewLayer,
) : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    override fun layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        previewLayer.setFrame(bounds)
        CATransaction.commit()
    }
}

/** QR 메타데이터 감지 델리게이트 */
private class QrScannerDelegate(
    private val onDetected: (String) -> Unit,
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection,
    ) {
        val metadata = didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject ?: return
        val rawValue = metadata.stringValue ?: return
        onDetected(rawValue)
    }
}
