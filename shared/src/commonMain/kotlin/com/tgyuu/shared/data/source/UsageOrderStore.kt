package com.tgyuu.shared.data.source

import com.tgyuu.shared.common.toCsv
import com.tgyuu.shared.common.toIntListFromCsv
import com.tgyuu.shared.platform.Settings

/**
 * 태그·반복 주기 최근 사용순 저장소 (Android LocalUserConfigDataSource의 usage order 대응).
 * 최근에 사용한 id가 리스트 맨 앞에 온다.
 */
class UsageOrderStore(private val settings: Settings) {

    fun tagUsageOrder(): List<Int> = order(KEY_TAG_USAGE_ORDER)

    fun recordTagUsage(tagId: Int) = record(KEY_TAG_USAGE_ORDER, tagId)

    fun removeTagUsage(tagId: Int) = remove(KEY_TAG_USAGE_ORDER, tagId)

    fun repeatCycleUsageOrder(): List<Int> = order(KEY_REPEAT_CYCLE_USAGE_ORDER)

    fun recordRepeatCycleUsage(cycleId: Int) = record(KEY_REPEAT_CYCLE_USAGE_ORDER, cycleId)

    fun removeRepeatCycleUsage(cycleId: Int) = remove(KEY_REPEAT_CYCLE_USAGE_ORDER, cycleId)

    /** 전체 초기화 시 태그·반복 주기 최근 사용순을 모두 비운다. */
    fun clearUsageOrder() {
        settings.remove(KEY_TAG_USAGE_ORDER)
        settings.remove(KEY_REPEAT_CYCLE_USAGE_ORDER)
    }

    private fun order(key: String): List<Int> =
        settings.getString(key, "").toIntListFromCsv()

    /** 사용한 [id]를 맨 앞으로 옮겨 최근 사용순을 유지한다. */
    private fun record(key: String, id: Int) {
        settings.putString(key, (listOf(id) + order(key).filter { it != id }).toCsv())
    }

    /** 삭제된 항목의 [id]를 최근 사용순에서 제거해 오펀 id가 남지 않도록 한다. */
    private fun remove(key: String, id: Int) {
        settings.putString(key, order(key).filter { it != id }.toCsv())
    }

    companion object {
        private const val KEY_TAG_USAGE_ORDER = "TAG_USAGE_ORDER"
        private const val KEY_REPEAT_CYCLE_USAGE_ORDER = "REPEAT_CYCLE_USAGE_ORDER"
    }
}
