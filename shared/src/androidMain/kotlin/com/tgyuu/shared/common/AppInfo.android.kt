package com.tgyuu.shared.common

import android.os.Build

// shared의 SettingScreen은 iOS에서만 사용되므로 Android 측 값은 비워둔다.
actual fun appVersionName(): String = ""

actual fun deviceName(): String = Build.MODEL ?: "Android"
