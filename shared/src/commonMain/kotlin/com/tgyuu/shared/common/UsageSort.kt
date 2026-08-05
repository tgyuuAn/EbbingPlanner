package com.tgyuu.shared.common

import com.tgyuu.shared.domain.model.DefaultRepeatCycles
import com.tgyuu.shared.domain.model.RepeatCycle
import com.tgyuu.shared.domain.model.TodoTag
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.TodoRepository

/**
 * 최근 사용 이력([usageOrder], 최근에 사용한 id가 앞) 순으로 목록을 재정렬한다.
 * 이력에 없는 항목은 기존 순서를 유지한 채 뒤로 배치된다.
 */
fun <T> List<T>.sortedByUsageOrder(usageOrder: List<Int>, idSelector: (T) -> Int): List<T> {
    val rank = usageOrder.withIndex().associate { (index, id) -> id to index }
    return sortedBy { rank[idSelector(it)] ?: Int.MAX_VALUE }
}

/** 태그 목록을 최근 사용순으로 정렬해 반환 (AddTodo/EditTodo 공통). */
suspend fun TodoRepository.loadTagsByUsage(config: ConfigRepository): List<TodoTag> =
    loadTags().sortedByUsageOrder(config.getTagUsageOrder()) { it.id }

/** 기본 + 커스텀 반복 주기를 합쳐 최근 사용순으로 정렬해 반환 (AddTodo/EditDate 공통). */
suspend fun TodoRepository.loadRepeatCyclesByUsage(config: ConfigRepository): List<RepeatCycle> =
    (DefaultRepeatCycles + loadRepeatCycles())
        .sortedByUsageOrder(config.getRepeatCycleUsageOrder()) { it.id }
