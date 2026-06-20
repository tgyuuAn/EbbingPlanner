package com.tgyuu.shared.common

import platform.Foundation.NSBundle

actual fun appVersionName(): String =
    (NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String) ?: ""
