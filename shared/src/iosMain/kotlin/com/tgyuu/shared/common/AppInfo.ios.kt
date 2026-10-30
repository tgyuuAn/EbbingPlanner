package com.tgyuu.shared.common

import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

actual fun appVersionName(): String =
    (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String) ?: ""

actual fun deviceName(): String = UIDevice.currentDevice.name
