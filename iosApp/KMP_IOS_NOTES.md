# KMP / iOS 작업 기록 & 재발 함정 노트

`feature/KMP` 브랜치(Compose Multiplatform 기반 iOS 포팅) 작업 로그와, 반복해서
마주친 에러·원인·해결을 남긴다. **새 작업/에러가 생기면 여기에 계속 추가할 것.**

---

## 재발 함정 (에러 → 원인 → 해결)

### 1. 소스 수정이 iOS 빌드에 반영 안 됨 (stale framework)
- **증상**: shared 코드를 고쳐도 앱에 반영 안 됨. `strings shared.framework`에 방금 넣은 문자열이 없음.
- **원인**: `gradle.properties`의 `org.gradle.configuration-cache=true`가 iOS 프레임워크 링크 태스크 산출물을 stale하게 복원. Xcode의 gradle 호출에도 동일 영향.
- **해결**: iOS 반복 작업 중에는 `configuration-cache=false`로 임시 전환, 작업 종료 후 `true`로 원복. 의심될 땐 `--no-configuration-cache`로 링크 강제.

### 2. 앱 실행 즉시 크래시 (첫 컴포지션에서 예외, "Error was captured in composition")
- **원인**: `shared`가 정적 프레임워크(`isStatic=true`)라 compose 리소스가 앱 번들에 포함되지 않음 → `stringResource`/`painterResource`가 런타임에 리소스를 못 찾아 예외.
- **해결**: Xcode "Build Shared Framework" 빌드 단계에 `./gradlew :shared:syncComposeResourcesForIos` 추가 (`iosApp/project.yml` + `EbbingPlanner.xcodeproj/project.pbxproj` 둘 다). 리소스가 앱 번들의 `compose-resources/composeResources/ebbingplanner.shared.generated.resources/...`로 복사됨.

### 3. 문자열에 포맷 지시자가 리터럴로 노출 (예: "오후 6시 %3$02d분", "50%%")
- **원인**: CMP(비-Android) `stringResource` 포매터가 `%02d`(폭/0채움)와 `%%`(퍼센트 이스케이프)를 미지원. `%1$s`/`%2$d` 같은 단순 positional만 치환.
- **해결**: 0채움은 Kotlin `padStart(2,'0')` 후 `%s`로 전달. `%%`는 리소스에서 `%`로 교체.

### 4. kotlinx-datetime 0.6 → 0.7 마이그레이션
- `Clock`/`Instant` → `kotlin.time`로 이동.
- `DayOfWeek`가 java.time typealias 아님: 정렬은 `isoDayNumber`, 로케일 표시명은 `java.time.DayOfWeek.of(isoDayNumber).getDisplayName(...)` (core.common `getDisplayName` 확장 사용).
- `LocalDate.toEpochDays()`가 Long 반환 → `daysUntil` 사용 권장.

### 5. AGP 9 + KMP
- shared 모듈은 `com.android.library` → `com.android.kotlin.multiplatform.library` 플러그인(버전 미지정, build-logic 클래스패스 AGP 사용).
- CMP 1.11은 iosX64(Intel 시뮬레이터) 미지원 → 타깃/ksp 제거 (iosArm64 + iosSimulatorArm64만).
- CMP 1.8+부터 material3가 material-icons 전이 의존 안 함 → `material-icons-core` 직접 의존.

### 6. 시뮬레이터 UI 시각 검증 방법 (탭 자동화 불가)
- System Events 접근성 권한 차단 + idb 미설치 → 좌표 탭 불가.
- **화면 캡처**: `DefaultRootComponent.initialConfiguration`을 임시로 대상 `Configuration`으로 변경 후 빌드→설치→(온보딩 소비 위해 1회 더)재실행→스크린샷. 끝나면 원복.
  - 단, `Configuration.Theme`는 Home으로 폴백돼 라우팅 안 됨(원인 미규명). `Configuration.Setting`, `Configuration.Widget`은 정상.
- 헬퍼 스크립트: `/tmp/run_ios.sh <out.png>` (config-cache off일 때만 신뢰 가능).
- 크래시 원인 추적: 초기엔 CMP가 컴포지션 예외를 잡아 stderr에 요약만 남김. `xcrun simctl launch --console`, 크래시 리포트(`~/Library/Logs/DiagnosticReports/EbbingPlanner*.ips`)의 faulting thread 백트레이스로 예외 지점 특정.

---

## 작업 로그 요약 (feature/KMP)

1. **develop 최신화**: origin/develop 병합(충돌 1건: app/build.gradle.kts) + AGP9/Kotlin2.3/Gradle9.6 마이그레이션 반영.
2. **iOS 실행 크래시 해결**: syncComposeResourcesForIos 추가 (위 함정 #2).
3. **cleanup 13건**: 중복 코덱/Saver/헬퍼 통합, expect/actual 축소, nullable configRepository 필수화, RootContent FQN 정리 등.
4. **UI 패리티 (Android↔iOS) high 26/26**: 워크플로로 91건 발견 → high 전량 수정. 상세·체크리스트는 `UI_PARITY.md`, 원본 발견 목록은 `UI_PARITY_FINDINGS.json`.
   - 주요: 알림시간 %02d, 정렬 라벨, AddTodo 자동포커스+키보드 imePadding, 날짜시트 패딩, 태그 색상 팔레트, 완료율 %%, 알림 헤더/카운터, 설정 섹션순서·초기화 다이얼로그·데이터 복원 행, 테마/위젯 제목·미리보기 카드·저장 버튼, WidgetNudge/notice/plus 드로어블 포팅, sync 타이포, 메모 미리보기 공용 카드.
   - medium: 라벨/레이아웃/타이포/로그 다수 반영 + 구조적(M14 테마선택기, M23/L22 색상 애니메이션, M27 반복삭제 다이얼로그 공용화, M30 메모 진입모드) 완료. 나머지는 플랫폼/의도적 차이로 기록.
   - low: analytics 로깅, verticalScrollbar 유틸 이식(L8/L9/L21), 등장/펼침 애니메이션(L4/L16/L22), 문구/간격 정렬 완료. 색 토큰 미세차 등은 무해로 기록.
   - 상세 완료/보류(사유)는 UI_PARITY.md의 MEDIUM/LOW 섹션 참조.
5. **신규 공용 유틸/헬퍼**: colorSchemeFor(테마별 색스킴), verticalScrollbar(Modifier), IntListCodec, UsageOrderStore 등.

## 검증 원칙
- 변경 후 `./gradlew :shared:compileKotlinIosSimulatorArm64`로 컴파일 확인.
- 가능하면 시뮬레이터 시각 검증(위 #6). 라우팅 불가 화면은 컴파일+Android 구조 미러+검증된 빌딩블록 근거로 수용하되 노트에 명시.
- 진단용 임시 변경(초기 라우트, config-cache)은 커밋 전 반드시 원복.
