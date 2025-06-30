package com.tgyuu.analytics

fun extractScreenName(route: String): String {
    // 1. 쿼리 제거: ? 뒤는 삭제
    val noQuery = route.substringBefore("?")

    // 2. 경로 제거: / 뒤는 삭제
    val noPath = noQuery.substringBefore("/")

    // 3. 패키지 제거: . 이 있으면 마지막만 사용
    return noPath.substringAfterLast(".")
}
