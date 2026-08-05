# iOS ↔ Android UI 패리티 점검 (화면/바텀시트/다이얼로그 전수)

iOS는 Android의 Compose Multiplatform 포팅. shared/ui/feature 의 iOS 구현을
feature/* 의 Android 원본과 1:1 비교해 맞춘다.

범례: ✅맞음 / 🔧수정함 / ⚠️차이있음(수정필요) / ❌iOS누락 / ⬜미점검

## 화면 (Screen)
| 화면 | Android | iOS(shared) | 상태 | 비고 |
|---|---|---|---|---|
| Onboarding | onboarding/OnboardingScreen | feature/onboarding/OnboardingScreen | ⬜ | |
| Home | home/graph/main/HomeScreen | feature/home/HomeScreen | ⬜ | |
| AddTodo | home/graph/addtodo | feature/home/addtodo | ⬜ | |
| EditTodo | home/graph/edittodo | feature/home/edittodo | ⬜ | |
| EditDate | home/graph/editdate | feature/home/editdate | ⬜ | |
| Notification | home/graph/notification | feature/home/notification | ⬜ | |
| Schedule(모아보기) | dashboard/ScheduleScreen | feature/schedule/ScheduleScreen | ⬜ | |
| Setting | setting/graph/main/SettingScreen | feature/setting/SettingScreen | ⬜ | 사용자 지목 |
| Theme | setting/graph/theme | feature/setting/theme | ⬜ | |
| Widget | setting/graph/widget | feature/setting/widget | ⬜ | |
| WebView | setting/graph/webview | (RootContent 인라인) | ⬜ | |
| Sync | sync/graph/main/SyncMainScreen | feature/sync/SyncScreen | ⬜ | |
| Restore | sync/graph/restore | feature/sync/restore | ⬜ | |
| Tag | tag/graph/main/TagScreen | feature/tag/TagScreen | ⬜ | |
| AddTag | tag/graph/addtag | feature/tag/addtag | ⬜ | |
| EditTag | tag/graph/edittag | feature/tag/edittag | ⬜ | |
| RepeatCycle | repeatcycle/graph/main | feature/repeatcycle | ⬜ | |
| AddRepeatCycle | repeatcycle/graph/addrepeatcycle | feature/repeatcycle/addrepeatcycle | ⬜ | |
| EditRepeatCycle | repeatcycle/graph/editrepeatcycle | feature/repeatcycle/editrepeatcycle | ⬜ | |
| Memo(Add/Edit) | memo/graph/addmemo, editmemo | feature/memo/MemoScreen | ⬜ | 구조 다름 |

## 바텀시트 (BottomSheet)
| 시트 | Android | iOS(shared) | 상태 |
|---|---|---|---|
| TagBottomSheet | home/graph/ui/bottomsheet | feature/home/addtodo/bottomsheet | ⬜ |
| RepeatCycleBottomSheet | home/graph/ui/bottomsheet | feature/home/addtodo/bottomsheet | ⬜ |
| SelectedDateBottomSheet | home/graph/ui/bottomsheet | feature/home/addtodo/bottomsheet | ⬜ |
| DelayBottomSheet | home/.../bottomsheet | feature/home/bottomsheet | ⬜ |
| DeleteBottomSheet | home/.../bottomsheet | feature/home/bottomsheet | ⬜ |
| OptionsBottomSheet | home/.../bottomsheet | feature/home/bottomsheet | ⬜ |
| SortTypeBottomSheet | home/.../bottomsheet | feature/home/bottomsheet | ⬜ |
| UpdateBottomSheet | home/.../bottomsheet | feature/home/bottomsheet | ⬜ |
| AlarmMessageBottomSheet | setting/graph/ui/bottomsheet | ❓ | ❌ |
| AlarmTimeBottomSheet | setting/graph/ui/bottomsheet | ❓ | ❌ |
| CalendarStartDayBottomSheet | setting/graph/ui/bottomsheet | ❓ | ❌ |
| ScheduleOptionsBottomSheet | dashboard/ui/bottomsheet | feature/schedule/bottomsheet | ⬜ |
| QrCodeBottomSheet | sync/.../bottomsheet | ❓ | ❌ |
| ColorBottomSheet | tag/ui/bottomsheet | feature/tag/bottomsheet | ⬜ |

## 다이얼로그 (Dialog)
| 다이얼로그 | Android | iOS(shared) | 상태 |
|---|---|---|---|
| ConfirmDelayAll | home/.../dialog | feature/home/dialog | ⬜ |
| ConfirmDelay | home/.../dialog | feature/home/dialog | ⬜ |
| ConfirmDeleteMemo | home/.../dialog | feature/home/dialog | ⬜ |
| ConfirmDeleteRemaining | home/.../dialog | feature/home/dialog | ⬜ |
| ConfirmDeleteSingle | home/.../dialog | feature/home/dialog | ⬜ |
| WidgetNudge | home/.../dialog | feature/home/dialog | ⬜ |
| InAppReview | home/.../dialog | ❓ | ❌ |
| ConfirmExit | home/graph/ui/dialog | ❓ | ❌ |
| AlarmTime(알림화면) | home/graph/notification/ui/dialog | ❓ | ❌ |
| SaveMemo | memo/ui/dialog | ❓ | ❌ |
| RepeatCycle Delete | repeatcycle/.../dialog | ❓ | ❌ |
| Tag Delete | tag/.../dialog | ❓ | ❌ |
| Camera Permission (sync) | sync/.../dialog | ❓ | ❌ |
| Confirm Disconnect (sync) | sync/.../dialog | ❓ | ❌ |
| Confirm SyncUp (sync) | sync/.../dialog | ❓ | ❌ |
| Setting ConfirmDelay | setting/graph/ui/dialog | ❓ | ❌ |

> ❌/❓ 는 파일명 기준 1차 추정. iOS는 인라인 구현했을 수 있으므로 코드 확인 후 확정.

## 진행 로그
- 설정 화면(실기기 캡처): **알림 시간이 `오후 6시 %3$02d분`로 깨짐** 발견 → 🔧수정.
  - 원인: CMP `stringResource`가 `%02d`(폭/0채움) 지시자 미지원. `alarm_time_format` = `%1$s %2$d시 %3$02d분`.
  - 조치: `AlarmFormat.alarmTimeText`에서 분을 `padStart(2,'0')` 후 `%3$s`로 전달. ko/en/ja 문자열 3개 `%3$02d`→`%3$s`.
  - 영향: 설정 알림시간, 알림시간 바텀시트, 알림설정 화면 등 동일 함수 사용처 전부.
- Setting 섹션 구조 차이: Android엔 `UpdateBody`(앱 업데이트) 있으나 iOS 없음 → 소프트/하드 업데이트는 iOS에서 remote config 미구현(TODO)이라 보류.
- 설정 바텀시트(알림시간/알림메시지/시작요일): 헤더·구조·패딩·버튼 Android와 일치 ✅.
  - 단, 알림메시지 미리보기 토큰 치환이 `"{할일}"` 하드코딩(en/ja 미치환) → 🔧`placeholderToken` 사용으로 수정(Android 동일).
- 검증 완료 후 초기 라우트/‌config-cache 원복함.

### 검증 방법(다음 턴 참고)
- 탭 자동화 불가(System Events 접근성 차단, idb 없음). 화면별 시각 검증은:
  1) `DefaultRootComponent`의 `initialConfiguration`를 임시로 대상 화면으로, `gradle.properties` config-cache 임시 off,
  2) `/tmp/run_ios.sh <out.png>` 로 빌드·설치, 이후 **재실행(온보딩 스킵)** 후 스크린샷,
  3) 검증 끝나면 두 임시변경 원복.
- 바텀시트/다이얼로그는 `MainViewController`에서 해당 Content 컴포저블을 EbbingTheme로 감싸 직접 렌더 후 스크린샷(탭 불필요).

## 완료/진행 요약
- ✅ 치명적: compose 리소스 미번들 크래시(앱 실행 불가) → syncComposeResourcesForIos 추가로 해결(별도 커밋).
- 🔧 설정: 알림시간 `%02d` 깨짐, 알림메시지 토큰 하드코딩.
- ⬜ 남은 대상: 홈/모아보기/태그/반복주기/동기화/메모 화면 및 각 바텀시트·다이얼로그 전수 대조.

## 워크플로우 전수 대조 결과 (91건: high 26 / med 30 / low 35)
전체 원본: iosApp/UI_PARITY_FINDINGS.json

### HIGH (사용자 눈에 보이는 차이/깨짐) — 체크박스로 진행 관리

- [ ] **알림 설정 화면 - 메시지 글자수 카운터/에러** (feature/home/notification/NotificationScreen.kt:176) — Android는 메시지 입력 영역 하단에 '글자수 카운터(%1$d / 50자)'와 에러 메시지(플레이스홀더 2회 이상, 50자 초과)를 항상 표시한다(Android NotificationScreen
- [x] **알림 설정 화면 - 헤더(넛지) 섹션 누락** (feature/home/notification/NotificationScreen.kt:73) — Android 화면 상단에는 큰 제목 '바쁜 날에도 복습일을 자동으로 챙겨드릴게요'(home_notification_nudge, heading24B)와 서브텍스트 '알림을 설정하면...'(home_
- [ ] **홈 메인 - 리스트 헤더 정렬 토글** (designsystem/model/SortTypeExt.kt:12) — 정렬 토글의 첫 번째(생성순/최신순) 라벨 문자열이 Android와 다르다. Android는 SortType.CREATED를 sort_latest("최신순")로 표시하는데(feature 홈 헤더가 
- [x] **할일 추가(AddTodo) / 할일 수정(EditTodo) 화면** (feature/home/addtodo/component/AddTodoComponents.kt:53) — 제목 입력 필드가 iOS에서 진입 시 자동 포커스/키보드 자동 노출이 안 된다. Android의 TitleContent(feature/home/.../ui/Title.kt:34-38)는 FocusR
- [x] **할일 추가(AddTodo) 화면 - 저장 버튼** (feature/home/addtodo/AddTodoScreen.kt:249) — 제목 입력으로 키보드가 올라오면 하단 저장 버튼('추가하기')이 키보드에 가려진다. Android AddTodoScreenPhone은 imePadding()을 버튼까지 포함하는 최상위 Column(
- [ ] **할일 수정(EditTodo) 화면 - 저장 버튼** (feature/home/edittodo/EditTodoScreen.kt:171) — Phone(!isWide) 레이아웃에서 imePadding()이 스크롤 Column(라인132)에만 적용되고 EbbingSolidButton('수정하기')은 imePadding 없는 바깥 Colum
- [x] **SelectedDateBottomSheet (일정 날짜 선택 시트)** (feature/home/addtodo/bottomsheet/SelectedDateBottomSheet.kt:23) — iOS SelectedDateBottomSheetContent은 startFromMonday 파라미터 자체가 없고 EbbingCalendar에 전달하지 않는다. iOS EbbingCalendar는 
- [x] **SelectedDateBottomSheet (일정 날짜 선택 시트)** (feature/home/addtodo/bottomsheet/SelectedDateBottomSheet.kt:40) — iOS는 EbbingCalendar에 horizontal=20.dp 패딩이 빠져 있다(padding(top=20, bottom=8)만 있음). Android는 padding(horizontal=20
- [x] **WidgetNudgeDialog (위젯 넛지 다이얼로그)** (feature/home/dialog/WidgetNudgeDialog.kt:74) — Android는 하단 영역에 위젯 미리보기 일러스트(ic_widget_nudge, 220x152dp 이미지)를 표시하는데, iOS는 이미지 대신 영어 리터럴 'Widget Preview' 텍스트를 
- [x] **ConfirmDelayAllDialog (이후 일정 모두 미루기 확인)** (feature/home/dialog/ConfirmDelayAllDialog.kt:109) — 미루기 버튼 콜백 인자가 Android와 다르다. Android는 onRightButtonClick = { onDelayClick(false) } 로 체크박스 상태와 무관하게 항상 false(쉬는 
- [x] **ConfirmExitDialog (작성 이탈 확인, AddTodo)** (feature/home/addtodo/AddTodoScreen.kt:315) — Android는 EbbingDialogIconTop 을 써서 상단에 ic_notice 아이콘 + 타이틀 + 서브텍스트(모두 중앙정렬 규격)를 표시하는데, iOS 인라인 구현은 아이콘이 전혀 없고 타
- [ ] **설정 메인 - 데이터 섹션 (DataBody)** (feature/setting/SettingScreen.kt:255) — Android DataBody에는 '데이터 복원'(sync_restore_title) 항목이 존재해 기기 ID 기반 복원 화면으로 진입하는 행이 있는데(SettingScreen.kt:744-763,
- [x] **설정 메인 - 섹션 배치 순서** (feature/setting/SettingScreen.kt:217) — 폰(compact) 레이아웃에서 섹션 순서가 Android와 다름. Android PhoneSettingScreen 순서: 알림 → 캘린더 → 태그/반복 → 테마 → 문의 → 안내 → 데이터 → 리
- [x] **데이터 초기화 다이얼로그 (ClearDataDialog / ConfirmClearDialog)** (feature/setting/SettingScreen.kt:602) — 다이얼로그 문구/구성이 완전히 다름. Android ConfirmClearDialog는 제목을 prefix+highlight+suffix로 조합('데이터를 ' + 붉은색 '초기화' + ' 하시겠습니
- [x] **테마 변경 화면 (ThemeScreen) - 상단바 타이틀** (feature/setting/theme/ThemeScreen.kt:56) — 상단바 타이틀이 Android는 setting_theme_change("테마 변경")인데 iOS는 setting_theme("테마")를 사용한다. 사용자에게 보이는 화면 제목이 다르다.
- [ ] **테마 변경 화면 - 미리보기 카드(PreviewBody)** (feature/setting/theme/ThemeScreen.kt:150) — Android 미리보기는 실제 TodoListCard(에빙 플래너 미리보기 샘플 할일 3개, 라이트/다크 각각 세로로 쌓인 카드, 좌하단 '라이트/다크' 라벨)로 렌더한다. iOS는 단순히 배경색만
- [x] **위젯 테마 변경 화면 (WidgetScreen) - 상단바 타이틀** (feature/setting/widget/WidgetScreen.kt:60) — Android 상단바 타이틀은 setting_widget_theme_change("위젯 테마 변경")인데 iOS는 widget_setting_title("위젯 설정")을 사용한다. 화면 제목이 다르
- [ ] **위젯 테마 변경 화면 - 저장 버튼 라벨/위치** (feature/setting/widget/WidgetScreen.kt:65) — Android는 하단 full-width EbbingSolidButton에 라벨 setting_apply("적용")를 쓴다. iOS는 기본(CONTROL) 상태에서 상단바 텍스트 링크로 home_s
- [ ] **위젯 테마 변경 화면 - 미리보기 카드(WidgetCard)** (feature/setting/widget/WidgetScreen.kt:174) — Android 미리보기는 실제 위젯 모양(라이트/다크 2개 카드, 상단 '오늘 할 일  0 /0' 배지+plus 아이콘, 하단 setting_no_schedule_today('금일 스케줄이 없어요.
- [x] **일정 화면 - 태그 카드 완료율 텍스트 (TagCard)** (feature/schedule/ScheduleScreen.kt:351) — 태그 헤더의 '완료율' 문구가 schedule_tag_count_completion 리소스를 쓰는데, 이 문자열이 세 로케일 모두 '완료율 %2$d%%'처럼 %% (퍼센트 이스케이프)를 포함한다. 
- [ ] **동기화 메인 - QR 카드 / 연동기기 / 마지막동기화 (description·기기명·날짜 등 주요 강조 텍스트)** (feature/sync/SyncScreen.kt:393) — Android가 heading16SB(16sp SemiBold)를 쓰는 강조 텍스트들을 iOS는 전부 headingSSB(18sp Bold)로 매핑했다. 폰트 크기(16→18sp)와 굵기(SemiB
- [x] **색상 선택 바텀시트 (ColorBottomSheet)** (feature/tag/bottomsheet/ColorBottomSheet.kt:59) — iOS의 색상 팔레트(TAG_COLORS, 라인 59-78)가 Android 원본(core/designsystem/.../ColorOptions.kt)과 완전히 다르다. Android는 순색 계열(
- [x] **태그 추가/수정 화면 저장 버튼 (AddTagScreen/EditTagScreen)** (feature/tag/addtag/AddTagScreen.kt:147) — 하단 저장 버튼 라벨이 Android와 다르다. Android AddTag는 tag_register_button("태그 등록하기"), EditTag는 tag_edit_button("태그 수정하기")
- [x] **반복 주기 추가/수정 화면 (하단 등록 버튼)** (feature/repeatcycle/addrepeatcycle/AddRepeatCycleScreen.kt:125) — 하단 저장 버튼 라벨이 Android와 다르다. Android 추가 화면은 하단 버튼에 repeat_register_button("반복 주기 등록하기"), 수정 화면은 repeat_edit_butt
- [ ] **메모 추가/수정 화면 - 미리보기(Preview) 카드** (feature/memo/MemoScreen.kt:265) — iOS는 MemoScreen 안에 자체 private TodoListCard(265~343줄)를 새로 그려 미리보기를 표시한다. Android 원본(MemoContent.kt PreviewConte
- [ ] **메모 추가/수정 화면 - 미리보기 카드 내 메모 표시** (feature/memo/MemoScreen.kt:320) — iOS 자체 카드는 메모를 light1 원형 안 'M' 글자 배지 + dark3 텍스트로 표시한다(320~340줄). Android/공용 카드에는 이런 'M' 배지가 존재하지 않으며, 메모는 lig

### 배치1 적용 (커밋)
- 🔧 정렬 라벨 최신순, AddTodo 자동포커스+저장버튼 imePadding, 날짜시트 패딩+월요일파라미터, DelayAll 동작, 테마/위젯 제목, 태그/반복 등록·수정 버튼 라벨.
- ✅ EditTodo 저장버튼: 오탐(이미 top Column imePadding 정상).
- ⬜ 보류: %% 완료율(H20, CMP동작 시각검증), 날짜시트 월요일 배선(VM state 필요).

### 배치2 적용 (커밋)
- 🔧 태그 색상 팔레트를 Android ColorOptions 값으로 통일(동기화 색 일치).
- 🔧 이탈 다이얼로그 제목 textAlign Center.
- ⬜ 남은 high: 알림화면 헤더/카운터(H1/H2), WidgetNudge 이미지(H9), 설정 복원행/섹션순서/초기화다이얼로그(H12~14), 테마·위젯 미리보기(H16/18/19), sync 타이포(H21), %%(H20), 메모 카드(H25/26), ConfirmExit 아이콘 — 드로어블/컴포넌트/네비 이식 필요분 포함, findings JSON 참조.

### 배치3 적용 (커밋)
- 🔧 완료율 %% → % (CMP), 알림화면 넛지 헤더 추가, 설정 섹션 순서 Android 일치(데이터→안내 뒤로), 초기화 다이얼로그 빨강 강조 제목+서브텍스트+뒤로 버튼.

### 배치4 적용 (커밋)
- 🔧 ic_widget_nudge / ic_notice 드로어블 iOS 포팅. WidgetNudge 'Widget Preview' 영어 리터럴 → 실제 위젯 미리보기 이미지. 이탈 다이얼로그 상단 notice 아이콘 추가.
