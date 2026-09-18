package com.tgyuu.deviceinfo

interface DeviceInfoProvider {
    suspend fun getDeviceName(): String

    /**
     * 현재 기기가 태블릿인지 휴대폰인지 판별합니다.
     *
     * 화면의 가장 짧은 변이 600dp 이상이면 태블릿으로 간주하며,
     * 이 기준은 안드로이드의 `sw600dp` 리소스 한정자와 동일합니다.
     * 폴더블처럼 펼침 상태에 따라 화면 크기가 달라지는 기기도 있으므로,
     * 값을 캐싱하지 않고 호출 시점의 구성을 매번 읽습니다.
     */
    fun getDeviceType(): DeviceType
}
