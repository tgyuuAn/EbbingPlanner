package com.tgyuu.home.model

/**
 * 최근 사용 이력([usageOrder], 최근에 사용한 id가 앞) 순으로 목록을 재정렬한다.
 * 이력에 없는 항목은 기존 순서를 유지한 채 뒤로 배치된다.
 */
fun <T> List<T>.sortedByUsageOrder(usageOrder: List<Int>, idSelector: (T) -> Int): List<T> {
    val rank = usageOrder.withIndex().associate { (index, id) -> id to index }
    return sortedBy { rank[idSelector(it)] ?: Int.MAX_VALUE }
}
