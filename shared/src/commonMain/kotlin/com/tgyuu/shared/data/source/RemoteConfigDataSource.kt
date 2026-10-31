package com.tgyuu.shared.data.source

/**
 * 원격 설정(Firebase Remote Config) 값 공급원.
 *
 * Android 는 core/network ConfigDataSource·FeatureFlagDataSource 가 FirebaseRemoteConfig 를 직접 쓰지만,
 * KMP 에는 Remote Config SDK 가 없어서 iOS 는 Swift(RemoteConfigBridge)가 람다로 값을 넘겨준다.
 * Firebase 가 설정되지 않은 환경에서는 [DefaultRemoteConfigDataSource] 로 기본값만 쓴다.
 */
interface RemoteConfigDataSource {
    /** fetch + activate. 실패해도 예외를 던지지 않고 마지막으로 활성화된 값을 쓰게 둔다. */
    suspend fun fetchAndActivate()

    /** 활성화된 문자열 값. 키가 없거나 비어 있으면 null. */
    fun getString(key: String): String?

    /** 활성화된 불리언 값. 키가 없거나 해석할 수 없으면 null. */
    fun getBoolean(key: String): Boolean? = when (getString(key)?.trim()?.lowercase()) {
        "true", "1", "yes", "on" -> true
        "false", "0", "no", "off" -> false
        else -> null
    }

    object Key {
        /**
         * Android 키(update_AN_release / hard_update_AN_release)에는 Android 버전이 들어 있어서
         * iOS 는 버전 번호 체계가 다른 별도 키를 쓴다. 값 형식은 Android UpdateInfo 참조 타입과 같다.
         *   {"minVersion":"1.2.0","noticeMsg":"..."}
         */
        const val IOS_UPDATE = "ios_update_info"
        const val IOS_HARD_UPDATE = "ios_hard_update_info"

        /** 업데이트 버튼이 여는 App Store 주소 (예: https://apps.apple.com/app/id0000000000) */
        const val IOS_APP_STORE_URL = "ios_app_store_url"
    }
}

/** Remote Config 미연동 환경: 항상 기본값(null)을 돌려준다. */
class DefaultRemoteConfigDataSource : RemoteConfigDataSource {
    override suspend fun fetchAndActivate() = Unit
    override fun getString(key: String): String? = null
}
