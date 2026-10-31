package com.tgyuu.shared.common

/**
 * Apple 하드웨어 모델 식별자 → 마케팅 모델명.
 * iOS 16부터 UIDevice.name 은 "iPhone" 같은 일반 이름만 주므로, Android 마케팅 모델명(DeviceInfoProvider)과
 * 같은 수준으로 보여주기 위해 사용해요. 표에 없는 새 기종은 식별자를 그대로 보여주고(Android Build.MODEL 폴백과 동일),
 * 새 기기가 나오면 이 표를 갱신해요.
 */
object AppleDeviceNames {

    fun displayName(identifier: String, fallback: String): String {
        if (identifier.isBlank()) return fallback
        return names[identifier] ?: identifier
    }

    private val names: Map<String, String> = buildMap {
        // iPhone (iOS 16 지원 기종 이후)
        put("iPhone10,1", "iPhone 8"); put("iPhone10,4", "iPhone 8")
        put("iPhone10,2", "iPhone 8 Plus"); put("iPhone10,5", "iPhone 8 Plus")
        put("iPhone10,3", "iPhone X"); put("iPhone10,6", "iPhone X")
        put("iPhone11,2", "iPhone XS")
        put("iPhone11,4", "iPhone XS Max"); put("iPhone11,6", "iPhone XS Max")
        put("iPhone11,8", "iPhone XR")
        put("iPhone12,1", "iPhone 11")
        put("iPhone12,3", "iPhone 11 Pro")
        put("iPhone12,5", "iPhone 11 Pro Max")
        put("iPhone12,8", "iPhone SE (2nd generation)")
        put("iPhone13,1", "iPhone 12 mini")
        put("iPhone13,2", "iPhone 12")
        put("iPhone13,3", "iPhone 12 Pro")
        put("iPhone13,4", "iPhone 12 Pro Max")
        put("iPhone14,4", "iPhone 13 mini")
        put("iPhone14,5", "iPhone 13")
        put("iPhone14,2", "iPhone 13 Pro")
        put("iPhone14,3", "iPhone 13 Pro Max")
        put("iPhone14,6", "iPhone SE (3rd generation)")
        put("iPhone14,7", "iPhone 14")
        put("iPhone14,8", "iPhone 14 Plus")
        put("iPhone15,2", "iPhone 14 Pro")
        put("iPhone15,3", "iPhone 14 Pro Max")
        put("iPhone15,4", "iPhone 15")
        put("iPhone15,5", "iPhone 15 Plus")
        put("iPhone16,1", "iPhone 15 Pro")
        put("iPhone16,2", "iPhone 15 Pro Max")
        put("iPhone17,3", "iPhone 16")
        put("iPhone17,4", "iPhone 16 Plus")
        put("iPhone17,1", "iPhone 16 Pro")
        put("iPhone17,2", "iPhone 16 Pro Max")
        put("iPhone17,5", "iPhone 16e")
        put("iPhone18,3", "iPhone 17")
        put("iPhone18,4", "iPhone Air")
        put("iPhone18,1", "iPhone 17 Pro")
        put("iPhone18,2", "iPhone 17 Pro Max")

        // iPad
        put("iPad6,11", "iPad (5th generation)"); put("iPad6,12", "iPad (5th generation)")
        put("iPad7,5", "iPad (6th generation)"); put("iPad7,6", "iPad (6th generation)")
        put("iPad7,11", "iPad (7th generation)"); put("iPad7,12", "iPad (7th generation)")
        put("iPad11,6", "iPad (8th generation)"); put("iPad11,7", "iPad (8th generation)")
        put("iPad12,1", "iPad (9th generation)"); put("iPad12,2", "iPad (9th generation)")
        put("iPad13,18", "iPad (10th generation)"); put("iPad13,19", "iPad (10th generation)")
        put("iPad15,7", "iPad (A16)"); put("iPad15,8", "iPad (A16)")

        put("iPad11,3", "iPad Air (3rd generation)"); put("iPad11,4", "iPad Air (3rd generation)")
        put("iPad13,1", "iPad Air (4th generation)"); put("iPad13,2", "iPad Air (4th generation)")
        put("iPad13,16", "iPad Air (5th generation)"); put("iPad13,17", "iPad Air (5th generation)")
        put("iPad14,8", "iPad Air 11-inch (M2)"); put("iPad14,9", "iPad Air 11-inch (M2)")
        put("iPad14,10", "iPad Air 13-inch (M2)"); put("iPad14,11", "iPad Air 13-inch (M2)")
        put("iPad15,3", "iPad Air 11-inch (M3)"); put("iPad15,4", "iPad Air 11-inch (M3)")
        put("iPad15,5", "iPad Air 13-inch (M3)"); put("iPad15,6", "iPad Air 13-inch (M3)")

        put("iPad11,1", "iPad mini (5th generation)"); put("iPad11,2", "iPad mini (5th generation)")
        put("iPad14,1", "iPad mini (6th generation)"); put("iPad14,2", "iPad mini (6th generation)")
        put("iPad16,1", "iPad mini (A17 Pro)"); put("iPad16,2", "iPad mini (A17 Pro)")

        put("iPad7,1", "iPad Pro 12.9-inch (2nd generation)"); put("iPad7,2", "iPad Pro 12.9-inch (2nd generation)")
        put("iPad7,3", "iPad Pro 10.5-inch"); put("iPad7,4", "iPad Pro 10.5-inch")
        listOf("iPad8,1", "iPad8,2", "iPad8,3", "iPad8,4").forEach { put(it, "iPad Pro 11-inch") }
        listOf("iPad8,5", "iPad8,6", "iPad8,7", "iPad8,8").forEach { put(it, "iPad Pro 12.9-inch (3rd generation)") }
        put("iPad8,9", "iPad Pro 11-inch (2nd generation)"); put("iPad8,10", "iPad Pro 11-inch (2nd generation)")
        put("iPad8,11", "iPad Pro 12.9-inch (4th generation)"); put("iPad8,12", "iPad Pro 12.9-inch (4th generation)")
        listOf("iPad13,4", "iPad13,5", "iPad13,6", "iPad13,7").forEach { put(it, "iPad Pro 11-inch (3rd generation)") }
        listOf("iPad13,8", "iPad13,9", "iPad13,10", "iPad13,11").forEach { put(it, "iPad Pro 12.9-inch (5th generation)") }
        put("iPad14,3", "iPad Pro 11-inch (4th generation)"); put("iPad14,4", "iPad Pro 11-inch (4th generation)")
        put("iPad14,5", "iPad Pro 12.9-inch (6th generation)"); put("iPad14,6", "iPad Pro 12.9-inch (6th generation)")
        put("iPad16,3", "iPad Pro 11-inch (M4)"); put("iPad16,4", "iPad Pro 11-inch (M4)")
        put("iPad16,5", "iPad Pro 13-inch (M4)"); put("iPad16,6", "iPad Pro 13-inch (M4)")
        put("iPad17,1", "iPad Pro 11-inch (M5)"); put("iPad17,2", "iPad Pro 11-inch (M5)")
        put("iPad17,3", "iPad Pro 13-inch (M5)"); put("iPad17,4", "iPad Pro 13-inch (M5)")
    }
}
