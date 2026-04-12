package com.tgyuu.analytics

private val screenNameMap = mapOf(
    "OnboardingRoute" to "Onboarding",
    "HomeRoute" to "Home",
    "AddTodoRoute" to "AddTodo",
    "EditDateRoute" to "EditDate",
    "EditTodoRoute" to "EditTodo",
    "AddMemoRoute" to "AddMemo",
    "EditMemoRoute" to "EditMemo",
    "ScheduleRoute" to "Schedule",
    "SettingRoute" to "Setting",
    "ThemeRoute" to "Theme",
    "WidgetRoute" to "WidgetTheme",
    "WebViewRoute" to "WebView",
    "TagRoute" to "Tag",
    "AddTagRoute" to "AddTag",
    "EditTagRoute" to "EditTag",
    "RepeatCycleRoute" to "RepeatCycle",
    "AddRepeatCycleRoute" to "AddRepeatCycle",
    "EditRepeatCycleRoute" to "EditRepeatCycle",
    "SyncMainRoute" to "SyncMain",
    "ConnectRoute" to "Connect",
)

/**
 * 주어진 네비게이션 route 문자열에서 화면 이름만 추출하고,
 * 매핑 테이블을 통해 분석용 화면 이름으로 변환합니다.
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
 * 4. 매핑 테이블에서 분석용 화면 이름을 조회합니다.
 *    예: "HomeRoute" → "HomeView"
 *
 */
fun mapScreenName(route: String): String {
    val noQuery = route.substringBefore("?")
    val noPath = noQuery.substringBefore("/")
    val className = noPath.substringAfterLast(".")
    return screenNameMap[className] ?: className
}
