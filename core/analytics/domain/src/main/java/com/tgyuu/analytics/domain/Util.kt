package com.tgyuu.analytics.domain

/**
 * 주어진 네비게이션 route 문자열에서 화면 이름만 추출합니다.
 *
 * 아래 순서로 문자열을 정리합니다:
 *
 * 1. 쿼리 파라미터 제거: `?` 뒤의 내용을 제거합니다.
 *    예: "HomeRoute?workedDate={workedDate}" → "HomeRoute"
 *
 * 2. 경로 세그먼트 제거: `/` 뒤의 내용을 제거합니다.
 *    예: "PlannerScreen/123" → "PlannerScreen"
 *
 * 3. 패키지 경로 제거: `.`이 포함된 경우, 마지막 클래스 이름만 추출합니다.
 *    예: "com.tgyuu.feature.planner.PlannerScreen" → "PlannerScreen"
 *
 */
fun extractScreenName(route: String): String {
    val noQuery = route.substringBefore("?")
    val noPath = noQuery.substringBefore("/")
    return noPath.substringAfterLast(".")
}
