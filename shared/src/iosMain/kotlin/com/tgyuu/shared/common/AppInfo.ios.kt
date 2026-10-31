package com.tgyuu.shared.common

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice
import platform.darwin.sysctlbyname
import platform.posix.size_tVar

actual fun appVersionName(): String =
    (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String) ?: ""

/**
 * iOS 16+ 의 UIDevice.name 은 "iPhone" 같은 일반 이름이라, 하드웨어 모델 식별자를 마케팅 모델명으로 바꿔 보여줘요.
 * (Android 는 마케팅 모델명 → 실패 시 Build.MODEL)
 */
actual fun deviceName(): String =
    AppleDeviceNames.displayName(
        identifier = hardwareModelIdentifier(),
        fallback = UIDevice.currentDevice.model,
    )

@OptIn(ExperimentalForeignApi::class)
private fun hardwareModelIdentifier(): String {
    // 시뮬레이터는 hw.machine 이 x86_64/arm64 라서 시뮬레이션 중인 기종 식별자를 환경변수에서 읽어요
    (NSProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"] as? String)
        ?.takeIf { it.isNotBlank() }
        ?.let { return it }

    return memScoped {
        val size = alloc<size_tVar>()
        if (sysctlbyname("hw.machine", null, size.ptr, null, 0u) != 0 || size.value == 0uL) return@memScoped ""
        val buffer = allocArray<ByteVar>(size.value.toInt())
        if (sysctlbyname("hw.machine", buffer, size.ptr, null, 0u) != 0) return@memScoped ""
        buffer.toKString()
    }
}
