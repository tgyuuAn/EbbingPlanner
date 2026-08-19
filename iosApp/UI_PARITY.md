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

- [x] **알림 설정 화면 - 메시지 글자수 카운터/에러** (feature/home/notification/NotificationScreen.kt:176) — Android는 메시지 입력 영역 하단에 '글자수 카운터(%1$d / 50자)'와 에러 메시지(플레이스홀더 2회 이상, 50자 초과)를 항상 표시한다(Android NotificationScreen
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
- [x] **설정 메인 - 데이터 섹션 (DataBody)** (feature/setting/SettingScreen.kt:255) — Android DataBody에는 '데이터 복원'(sync_restore_title) 항목이 존재해 기기 ID 기반 복원 화면으로 진입하는 행이 있는데(SettingScreen.kt:744-763,
- [x] **설정 메인 - 섹션 배치 순서** (feature/setting/SettingScreen.kt:217) — 폰(compact) 레이아웃에서 섹션 순서가 Android와 다름. Android PhoneSettingScreen 순서: 알림 → 캘린더 → 태그/반복 → 테마 → 문의 → 안내 → 데이터 → 리
- [x] **데이터 초기화 다이얼로그 (ClearDataDialog / ConfirmClearDialog)** (feature/setting/SettingScreen.kt:602) — 다이얼로그 문구/구성이 완전히 다름. Android ConfirmClearDialog는 제목을 prefix+highlight+suffix로 조합('데이터를 ' + 붉은색 '초기화' + ' 하시겠습니
- [x] **테마 변경 화면 (ThemeScreen) - 상단바 타이틀** (feature/setting/theme/ThemeScreen.kt:56) — 상단바 타이틀이 Android는 setting_theme_change("테마 변경")인데 iOS는 setting_theme("테마")를 사용한다. 사용자에게 보이는 화면 제목이 다르다.
- [x] **테마 변경 화면 - 미리보기 카드(PreviewBody)** (feature/setting/theme/ThemeScreen.kt:150) — Android 미리보기는 실제 TodoListCard(에빙 플래너 미리보기 샘플 할일 3개, 라이트/다크 각각 세로로 쌓인 카드, 좌하단 '라이트/다크' 라벨)로 렌더한다. iOS는 단순히 배경색만
- [x] **위젯 테마 변경 화면 (WidgetScreen) - 상단바 타이틀** (feature/setting/widget/WidgetScreen.kt:60) — Android 상단바 타이틀은 setting_widget_theme_change("위젯 테마 변경")인데 iOS는 widget_setting_title("위젯 설정")을 사용한다. 화면 제목이 다르
- [x] **위젯 테마 변경 화면 - 저장 버튼 라벨/위치** (feature/setting/widget/WidgetScreen.kt:65) — Android는 하단 full-width EbbingSolidButton에 라벨 setting_apply("적용")를 쓴다. iOS는 기본(CONTROL) 상태에서 상단바 텍스트 링크로 home_s
- [x] **위젯 테마 변경 화면 - 미리보기 카드(WidgetCard)** (feature/setting/widget/WidgetScreen.kt:174) — Android 미리보기는 실제 위젯 모양(라이트/다크 2개 카드, 상단 '오늘 할 일  0 /0' 배지+plus 아이콘, 하단 setting_no_schedule_today('금일 스케줄이 없어요.
- [x] **일정 화면 - 태그 카드 완료율 텍스트 (TagCard)** (feature/schedule/ScheduleScreen.kt:351) — 태그 헤더의 '완료율' 문구가 schedule_tag_count_completion 리소스를 쓰는데, 이 문자열이 세 로케일 모두 '완료율 %2$d%%'처럼 %% (퍼센트 이스케이프)를 포함한다. 
- [x] **동기화 메인 - QR 카드 / 연동기기 / 마지막동기화 (description·기기명·날짜 등 주요 강조 텍스트)** (feature/sync/SyncScreen.kt:393) — Android가 heading16SB(16sp SemiBold)를 쓰는 강조 텍스트들을 iOS는 전부 headingSSB(18sp Bold)로 매핑했다. 폰트 크기(16→18sp)와 굵기(SemiB
- [x] **색상 선택 바텀시트 (ColorBottomSheet)** (feature/tag/bottomsheet/ColorBottomSheet.kt:59) — iOS의 색상 팔레트(TAG_COLORS, 라인 59-78)가 Android 원본(core/designsystem/.../ColorOptions.kt)과 완전히 다르다. Android는 순색 계열(
- [x] **태그 추가/수정 화면 저장 버튼 (AddTagScreen/EditTagScreen)** (feature/tag/addtag/AddTagScreen.kt:147) — 하단 저장 버튼 라벨이 Android와 다르다. Android AddTag는 tag_register_button("태그 등록하기"), EditTag는 tag_edit_button("태그 수정하기")
- [x] **반복 주기 추가/수정 화면 (하단 등록 버튼)** (feature/repeatcycle/addrepeatcycle/AddRepeatCycleScreen.kt:125) — 하단 저장 버튼 라벨이 Android와 다르다. Android 추가 화면은 하단 버튼에 repeat_register_button("반복 주기 등록하기"), 수정 화면은 repeat_edit_butt
- [x] **메모 추가/수정 화면 - 미리보기(Preview) 카드** (feature/memo/MemoScreen.kt:265) — iOS는 MemoScreen 안에 자체 private TodoListCard(265~343줄)를 새로 그려 미리보기를 표시한다. Android 원본(MemoContent.kt PreviewConte
- [x] **메모 추가/수정 화면 - 미리보기 카드 내 메모 표시** (feature/memo/MemoScreen.kt:320) — iOS 자체 카드는 메모를 light1 원형 안 'M' 글자 배지 + dark3 텍스트로 표시한다(320~340줄). Android/공용 카드에는 이런 'M' 배지가 존재하지 않으며, 메모는 lig

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

### 배치5 적용 (커밋)
- 🔧 메모 미리보기를 자체 카드('M' 배지)에서 공용 TodoListCard로 교체(Android 동일, 홈/투두와 동일 카드).

### 배치6 적용 (커밋)
- 🔧 sync 화면 강조 텍스트 headingSSB(18B) → bodyMSB(16SB), Android heading16SB와 크기/굵기 일치.

### 배치7 적용 (커밋)
- 🔧 설정 데이터 섹션에 "데이터 복원" 행 추가(기기ID 복원 화면 진입). Contract/VM/RootContent 배선.

### 배치8 적용 (커밋)
- 🔧 알림 설정 화면 메시지 글자수 카운터(%1$d / 50자)+검증 에러(플레이스홀더 2회↑, 50자 초과) 추가, 입력 limit 100→50, 미리보기 토큰 로케일화.

### 배치9 적용 (커밋)
- 🔧 위젯 화면 저장 버튼 항상 하단 적용, 미리보기를 실제 위젯 카드(오늘 할 일 0/0+plus+빈 문구, alpha 반영)로 재구성. 시각 검증 완료.

## 최종 요약 (high 26건 기준)
- ✅ 완료(25건): 알림시간 %02d, 정렬 라벨, 자동포커스, AddTodo 키보드/버튼, 날짜시트 패딩, DelayAll 동작,
  이탈 다이얼로그 아이콘/정렬, 태그 색상 팔레트, 완료율 %%, 알림 헤더/카운터, 설정 섹션순서/초기화 다이얼로그/복원행,
  테마·위젯 화면 제목, 위젯 저장버튼/미리보기, WidgetNudge 이미지, sync 타이포, 메모 카드, 태그/반복 버튼 라벨 등.
- ✅ 완료(26/26): H16 테마 미리보기 포함 전체 high 반영
- (구)남음: **H16 테마 미리보기 카드** — Android는 실제 TodoListCard 샘플(라이트/다크)로 렌더. 현재 iOS는 테마 색
  박스+라벨. 완전 재구성은 샘플 모델/중첩 EbbingTheme 처리가 필요해 blind 편집 리스크가 큼 → 시각 반복 검증하며 별도 진행 권장.
- med 30 / low 35: findings JSON 참조, 후속.

### 배치10 (최종) 적용 (커밋)
- 🔧 H16 테마 미리보기: 색 박스+라벨 → 라이트/다크 각각 실제 샘플 TodoListCard(테마 적용)로 재구성. Android PreviewBody 대응. **high 26/26 완료.**

## MEDIUM 진행 (반복 방지용 체크)
완료(✅): M2 알림토글라벨, M10 이탈다이얼로그아이콘(=H11), M11 동기화행라벨, M13 테마헤드라인,
M15 위젯헤드라인, M16 내용투명도라벨, M17 일정옵션정렬, M19 QR버튼타이포, M22 색상시트제목,
M24 태그이름 카운터제거+limit20, M25 태그색상행, M28 메모구분선제거, M29 메모라벨간격,
+ 홈 캘린더 analytics 로깅(로그) 배선.

보류/사유(⬜): 
- M1 알림 24h밑줄: iOS는 12h "오후 6시 30분"로 설정과 일관 유지(제품 선택). 밑줄 어포던스 추후.
- M3/M4/M5 알림 플레이스홀더안내/미리보기카드스타일/하단CTA: M-메시지 카운터(H1)로 일부 반영. 미리보기 fillNormal 카드화·하단 '일정 등록' CTA는 진입경로(설정탭 전용) 확인 필요 → 보류.
- M6 온보딩 카피: iOS/develop 신규 카피로 추정 → 제품 확인 후 정렬.
- M7 캘린더 sync 아이콘(ic_link vs 코드아이콘): 경미, 추후 에셋 통일.
- M8 헤더 월↔주 nested scroll: iOS는 리스트 스크롤 기반으로 동작(방식 상이하나 기능 정상).
- M9 카드 제목/메모 URL 클릭텍스트: EbbingClickableText(링크 파싱) 포팅 필요 → 별도 기능.
- M12 버전/업데이트 행: iOS remote config 미구현 → 의도적.
- M14 테마 선택 체크아이콘/원색: 유니코드 체크 사용 중. ic_check+primaryNormal화 추후.
- M18 일정화면 태그삭제 다이얼로그 문구: 공용 문구 통일 가능성 → 확인 후.
- M20 sync 메인 복원 진입: 복원 진입은 설정에 추가함(H12). sync 메인 위치는 별도.
- M21 카메라 권한 분기: iOS/Android 권한모델 상이 → 의도적.
- M23 색상시트 선택 애니메이션/스크롤바: verticalScrollbar 유틸 미포팅 → 추후.
- M26 반복 저장버튼 실험분기 / 테마·태그도 동일: 아래 배치로 always-bottom 정렬 검토.
- M27 반복 삭제 다이얼로그 EbbingDialogDefaultTop화: 스타일 미세, 추후.
- M30 메모 Add/Edit 모드 플래그: VM state 필요 → 별도.

### 저장버튼 실험 정렬 (M26+테마/태그)
- 🔧 AddTag/EditTag/AddRepeatCycle/EditRepeatCycle/Theme: 상단 CONTROL 저장 링크 숨김 + 하단 버튼 항상 노출(Android 실험 제거·항상 하단과 일치). 위젯은 H18에서 완료.

### 구조적 medium 진행
- ✅ M14 테마 선택기: 원 색상 theme.lightBg→해당 테마 primary(colorSchemeFor 헬퍼 신설), 선택 표시 유니코드 체크→ic_check 아이콘. EbbingTheme도 colorSchemeFor 재사용하도록 정리.
- ✅ M27 반복 삭제 다이얼로그: 수동 재구현 → 공용 EbbingDialogDefaultTop(AnnotatedString)+EbbingDialogBottom 슬롯으로 교체(Android 간격 spacedBy8/top40 bottom12, subText bodySM 일치).
- ⬜ M9 카드 제목/메모 URL 클릭텍스트: URL 파서 + 플랫폼 URL 오프너(expect/actual) 필요한 기능 → 별도 작업으로 보류(스타일만은 무의미).
- 🔧 M23 색상 시트 선택 애니메이션: 선택 시 색 어둡게(lerp)+체크 페이드(animateColorAsState/AnimatedVisibility). 스크롤바 유틸·원크기(45dp)는 별도 보류(verticalScrollbar 미포팅).
- ✅ M30 메모 Add/Edit 모드: memo 공백 휴리스틱 → nav 진입 모드(isEditEntry) 플래그로 판정. State/VM/화면/RootContent wrapper 배선(Add=false, Edit=true).
- 구조적 medium 요약: M14/M23/M27/M30 ✅, M8 iOS 방식 정상, M9 보류(URL 오프너 기능 필요).

## LOW 진행/분류 (반복 방지)
완료(✅): L2 홈 analytics(로그, 앞서 배치), L18 마지막동기화 문구 정렬, L27 태그 저장버튼 실험(앞서 배치),
L29 태그 헤드라인 top패딩 제거, L35 메모 wide 미리보기 상단 Spacer 제거.
무해/의도적(스킵, 사유):
- 색 토큰 미세차(L3 완료카운트, L6 EditDate 설명, L10 반복안내, L19 구분선, L25 sync 로딩, L33 반복 빈상태):
  프로젝트 토큰 매핑(textDisabled↔dark3, primaryNormal↔primaryDefault 등)상 실색 동일/의도적. 값 다르면 후속.
- 아이콘 Icon+tint vs Image원본(L5,L14,L23,L24,L34): 대상 벡터가 단색이라 시각 동일(무해).
- L1 온보딩 이미지 tint/정렬: develop 신규 온보딩 디자인 추정(제품 확인).
- L7 하드웨어/제스처 백 확인 다이얼로그: iOS 제스처 백 모델 상이(플랫폼).
- L11 +아이콘 contentDescription: iOS가 오히려 개선(접근성) — 변경 불필요.
- L12 요일 정렬 키(ordinal vs isoDayNumber): 결과 동일.
- L15 알림 토글 OS 권한 동기화: iOS 권한 모델 상이(플랫폼).
- L20 일정옵션 서브타이틀 originalText: iOS title이 순수 String이라 결과 동일.
- L26 QR 남은시간: 코드에서 %02d 처리(정상).
- L28 태그삭제 하이라이트 키: 값 동일("삭제").
- L30 리스트 state 미지정 / L32 반복 리스트 "- " 리터럴: 결과 동일.
- L31 반복 삭제 타이틀 타이포: M27로 공용 컴포넌트화되어 해소.
보류(별도 작업 필요):
- ✅ L8/L9/L21 verticalScrollbar: shared designsystem/util/Scrollbar.kt 신설(LazyList/LazyGrid 오버로드) 후 태그/반복/일정색상 시트에 적용.
- ✅ L22 일정 색상 그리드 선택 애니메이션(animateColorAsState+체크 페이드). ✅ L4 AddTodo 일정카드 등장 애니메이션(AnimatedVisibility), ✅ L16 설정 알림행 펼침 애니메이션(AnimatedVisibility+Column). → 애니메이션 low 전부 반영.

## 문자열 리소스 이스케이프 전수조사 (사용자 지목: 일정추가/편집 Title `""`)
compose-resources 빌드타임 변환기(`convertXmlValueResources`→`.cvr`)는 Android
aapt 관례를 미지원 → commonMain composeResources로 복사된 문자열의 이스케이프가
값에 그대로 저장돼 iOS에 리터럴 노출. `.cvr`(=`string|name|base64(UTF-8)`)
디코드로 저장 바이트를 직접 검증하며 전수 처리:
- ✅ 래핑 큰따옴표 `"..."` 77건 제거(values 28/en 25/ja 24/ko 0). 예: `home_add_todo_header_suffix`. 선행 공백/`\n` 보존 확인(cvr 첫 바이트 0x20). commit 6f30781c.
- ✅ 이스케이프 아포스트로피 `\'` 41건 → `'`(values 6/en 33/ja 2). 예: `schedule_tag_edit_title` = `'%1$s' 태그 편집`. commit a248b2b8.
- ✅ `\"` 0건(없음), `\n`은 변환기가 정상 처리(건드리지 않음), `%%`·`%02d`는 앞선 작업에서 처리(note #3).
- ✅ 큰따옴표 strip 시 내부 표시용 따옴표 오손 없음(제거분 전량 값 전체 래핑=공백보존용). XML 4파일 well-formed 재확인.
- ✅ iOS 빌드/실행 무회귀(온보딩 정상 렌더, 스크린샷). 라우팅 제약으로 AddTodo 화면 직접 캡처 대신 cvr 바이트 검증으로 확정.
- 규칙: 새 문자열은 aapt 이스케이프(따옴표/백슬래시) 쓰지 말고 순수 텍스트로. 상세 KMP_IOS_NOTES.md 함정 #7.

## AddTodo / EditTodo 동작 패리티 전수조사 (사용자 지목 화면, 2차)
Android 원본(feature/home/graph/addtodo·edittodo) ↔ iOS(shared/ui/feature/home/addtodo·edittodo) 정밀 비교.
서브에이전트 발견 + 직접 검증. **미구현은 위험도/범위 때문에 기록만; 안전한 것만 즉시 반영.**

### 대형 플랫폼 기능 (✅ 2026-08-20 구현 완료)
- ✅ **알림 예약 구현(iOS)**: AddTodo 저장 시 저장된 알림 설정(enabled)·시간으로 각 미래 일정에 `NotificationScheduler`(UNUserNotificationCenter) 로컬 알림 등록. id=date.hashCode(), body=문구 {할일}→제목 치환, 과거 스킵. NotificationScheduler를 Koin DI(Android/IosModule)에 등록. **의도적 차이**: Android는 무조건 스케줄하지만 iOS는 enabled일 때만(권한·사용자 선택 존중). EditTodo 날짜변경 재예약은 별도(미포함).
- ✅ **알림 넛지 페이지 구현(iOS)**: `shouldShowNotificationNudge()`(최초 1회 소비형) true면 AddTodo가 Page.NOTIFICATION으로 전환. AddTodoContract에 Page/NotificationState/알림 인텐트, VM에 initNotificationState·onNotificationSaveClick·scheduleAlarms, UI는 addtodo/AddTodoNotificationNudge.kt. 토글 기본 off(Android 동일), 토글 on 시 iOS 권한 요청, off면 상세 접힘. 임시 라우팅 스크린샷으로 렌더 검증. 상세 KMP_IOS_NOTES.md 함정/로그 #6.

### 시스템적 갭 (기록 — 범위 큼)
- ⚠️ **SCREEN_VIEW/Click 애널리틱스 광범위 누락**: iOS 포트는 화면 진입 `screen_view`를 어디서도 안 남김(RootContent 중앙 로깅 없음). AddTodo/EditTodo의 View/Click 로그도 없음. `AnalyticsHelper`(platform/Analytics.kt)·패턴(ScheduleViewModel.logClick, HomeScreen homeClickEvent) 존재 → 화면별 VM에 analyticsHelper 주입+로깅 필요(교차절단, RootContent 배선 동반). 사용자 "로그 전부" 요청 대상 → 우선순위 높음.

### 소규모 파리티 (개별 처리)
- ⚠️ **미지정 태그 로컬라이즈 이름 누락**: Android는 기본 태그명을 `R.string.tag_unassigned`("미지정")로 치환(init/initLastSelected/기본선택 3곳). iOS는 DB 원본명 그대로. shared strings에 `tag_unassigned` **없음**(Android core/designsystem res에만) → 4개 로케일 추가 후 적용 필요.
- ⚠️ **mondayStart(주 시작요일) 미배선**: Android는 `configRepository.getMondayStart()`를 날짜·반복 바텀시트에 `startFromMonday`로 전달. iOS AddTodoState/EditTodoState에 필드 없고 시트에 미전달. shared에 `getMondayStart()` 존재 → 배선 가능.
- ⚠️ **EditTodo "태그 추가" no-op**: Android `onAddTagClick`은 시트 닫고 AddTag로 이동. iOS `OnAddTagClick`은 빈 스텁. + 복귀 시 `loadNewTag()`로 신규 태그 자동선택도 iOS 없음.
- ⚠️ **EditTodo null 태그 처리**: Android는 loadTag null이면 뒤로가기. iOS는 tag=null로 화면 유지, 저장 시 조용히 무동작(버튼은 활성). 피드백 필요.
- ⚠️ **first-todo 위젯 넛지/등록카운트**: Android는 `markFirstTodoAdded()` 결과를 HomeRoute `showWidgetNudge`로 전달 + `incrementTodoRegisteredCount()`. iOS는 결과 무시, 카운트 미증가.
- 🔧 **저장 시 clearFocus 누락**: Android 저장 onClick은 `focusManager.clearFocus()`로 키보드 닫음. iOS는 인텐트만. → 즉시 반영 예정(격리·안전).
- ⚠️ **TitleContent 포커스 자동스크롤 누락**: Android는 포커스 시 `animateScrollWhenFocus`로 스크롤. iOS TitleContent는 scrollState 미수용.

### 저위험/무해
- iOS ScheduleContent 이중 AnimatedVisibility(컴포넌트 내부 가드와 중복) — 애니메이션 미세차(L).
- 헤드라인 하이라이트 색: Android textPrimary 밑줄(무채움) vs iOS primaryDefault 하이라이트 — 시각 강조차(L).
- 태블릿 폼 좌우 패딩 40dp(Android) vs 20dp(iOS) (L).

## EditDate / Notification / Schedule 동작 패리티 전수조사 (3차)
Android(feature/home/graph/editdate·notification, feature/schedule/dashboard) ↔ iOS(shared) 정밀 비교.

### EditDate
- ⚠️(high) **스낵바 전부 무동작**: iOS EditDateViewModel `onShowSnackbar` 기본 `{}`이고 RootContent 생성부(477-484)에서 미전달 → no-schedule/missing-tag/date-repeat-changed/update-failed/all-rest-days 스낵바 안 뜸. 근본은 iOS 포트에 스낵바 호스트 미배선(Schedule도 `onShowSnackBar = { /* TODO */ }`)=시스템적. 스낵바 호스트 배선 후 각 VM 연결 필요.
- ⚠️(high) **"반복주기 직접 추가" no-op**: Android는 AddRepeatCycle로 이동+복귀 시 `loadNewRepeatCycle()`로 신규 선택. iOS `OnAddRepeatCycleClick` 빈 스텁, 미배선. (EditTodo "태그 추가" no-op과 동류.)
- ⚠️(med) EditDate Save Click 애널리틱스 누락.
- ⚠️(low) onSaveClick 검증 순서 Android(tag→empty) vs iOS(empty→tag).
- ⚠️(low/med) imePadding 범위 차 → iOS Save 버튼이 키보드에 가림(외곽 Column 아닌 내부에만). 🔧 clearFocus는 앞서 반영.
- ⚠️(low/med) mondayStart 시트 미배선(AddTodo/EditTodo와 동일 계열).
- ⚠️(low) ScheduleCheckContent 등장/퇴장 애니메이션(EbbingVisibleAnimation) 누락.

### Notification (충실한 포팅 아님 — 근본적으로 다른 화면)
- ⚠️(high) **아키텍처/저장 모델 상이**: Android는 AddTodo 플로우 내 "NotificationNudge" 페이지(하단 Save로 설정+투두 저장 후 홈 이동, Save 시에만 영속). iOS는 독립 설정화면(토글/시간/문구/초기화가 즉시 config에 write, Save 버튼·투두저장 없음). → 알림 예약 미구현(2차 기록)과 함께 재설계 결정 필요.
- ⚠️(high) **디테일 섹션 항상 표시**: Android는 토글 off면 AnimatedVisibility로 접힘. iOS는 `isNotificationEnabled` 무관하게 항상 렌더.
- ⚠️(med) 기본값 반전: Android `notificationEnabled=false` vs iOS `isNotificationEnabled=true`(config null 폴백 true).
- ⚠️(med) 초기화 버튼 항상 표시(Android는 message!=default일 때만).
- ⚠️(med) **미리보기 항상 렌더(무효 시 깨짐)**: Android는 `isValidPlaceholder && preview.isNotEmpty()`일 때만, placeholder 2+개면 preview="". iOS는 무조건 렌더+토큰 replace → 플레이스홀더 2+개면 깨진 미리보기.
- ⚠️(med) 문구 길이 입력 캡 불일치: iOS `limit=50` 하드캡인데 VM은 100까지 허용(내부 불일치). Android는 캡 없고 50 초과 시 에러.
- ⚠️(med) 저장/토글/시간/초기화 Click 애널리틱스 누락.
- ⚠️(low/med) 미리보기 카드 스타일(Android 라운드 카드+캡션 vs iOS 평문), 섹션 구분(스페이서 vs Divider), 플레이스홀더 설명 볼드 강조 차이.

### Schedule (모아보기) — 충실한 포팅, 실질 갭 1건
- 🔧(med) **onDelayAll 배치화**: iOS 개별 updateTodo 루프 → Android처럼 `updateTodos(updated)` 배치(원자적·성능). **이번에 반영.** (onDeleteRemaining은 양쪽 forEach라 파리티.)
- ⚠️(low) 태그삭제 다이얼로그 제목 구성(iOS buildAnnotatedString 강조) — 메시지 동등, 시각 강조차.
- 참고: 삭제/미루기/메모 액션 Click 로그는 Android도 없음(=파리티). 옵션시트는 iOS도 다이얼로그 전 hide함(스택버그 없음).

## Tag / Memo / RepeatCycle / Sync 동작 패리티 전수조사 (4차)

### Sync (가장 상이하나 상당수 의도적)
- ✅**의도적**: 오프라인 네트워크 게이팅 차이는 설계 결정. `AutoBackupManager.kt:21` 주석 — "Android NetworkMonitor 게이팅은 iOS에선 syncUpData 실패→pending 재시도로 대체". iOS엔 NetworkMonitor 없음. syncUp/disconnect/generateCode/qr/restore가 오프라인에서도 호출되지만 실패 시 재시도 모델. → **수정 안 함**(플랫폼 적합 설계).
- ⚠️(med) NetworkBanner(연결/해제 애니메 배너) iOS 없음. 에러 세분화(isNetworkError→전용 문구, raw error.message) iOS는 액션별 단일 문구.
- ⚠️(med) Restore 진입점: iOS는 AdvancedInfo에 linkedUuid==null일 때 "데이터 복원" 행 노출(SyncScreen.kt:608-634). Android SyncMain엔 해당 UI/인텐트 없음 → 다른 라우트로 진입. nav 그래프 확인 필요.
- ⚠️(med) SyncMain/다이얼로그/Restore 버튼 Click 애널리틱스 누락.

### Tag
- 🔧(med) **기본 태그 색상**: iOS `DEFAULT_TAG_COLOR=0xFFFF6B6B`(빨강) → Android처럼 `DefaultTodoTag.color`(0xFFBBE1FA 연파랑)로 변경. **이번 반영.** (Add/Edit 둘 다.)
- ✅**유지**: EditTag "변경 없음" 가드(iOS는 name/color 변경 시에만 Update 활성) — iOS가 더 엄격/우수. Android는 no-op 저장 허용. iOS 유지.
- ⚠️(med) Tag Back/Add/Delete/Edit·Save Click 애널리틱스 누락.
- ⚠️(low) iOS deleteTag에 Android의 방어적 id==1 가드 없음(버튼 도달 불가라 무해). ColorBottomSheet 스와치 40dp/8dp/스크롤바 없음(Android 45/10/있음).

### RepeatCycle
- ✅**유지(차이 기록)**: Edit 저장버튼, iOS는 `intervals.isNotEmpty() && parsedIntervals.isNotEmpty()`(공백/콤마만이면 비활성·무피드백), Android는 텍스트 있으면 활성+무효 시 스낵바. iOS가 더 엄격하나 무피드백 → 스낵바 호스트 배선 후 Android식 피드백 고려. 현재 유지.
- ⚠️(med) 3개 화면 Click 애널리틱스 누락. ⚠️(low) 저장 시 clearFocus 미이식. 참고: SaveButtonPosition 실험은 양쪽 하드코딩(if(false)/if(true))이라 무효과.

### Memo (대체로 충실, iOS는 add/edit 단일화)
- 🔧(med) **compact 레이아웃 스크롤 없음**: Android는 compact 콘텐츠 Column에 `verticalScroll`. iOS는 평 Column이라 키보드/작은화면서 미리보기+60dp Spacer 클리핑 가능 → verticalScroll 추가. **이번 반영.**
- ⚠️(med) Back/Save·(Add전용)SaveMemoSingle/All Click 애널리틱스 누락.
- ✅ iOS가 오히려 우수: 저장 실패 스낵바(snack_memo_save_failed 등) Android엔 없음.
- ⚠️(low) isSaveEnabled: Android isNotEmpty vs iOS isNotBlank(공백만 비활성, 무해). clearFocus 미이식.

## 잔여 백로그 일괄 처리 (5차, 2026-08-20)
사용자 "남은것들 계속 다 해" 지시로 백로그 대부분 반영.
- ✅ **EditTodo 날짜변경 알림 재예약**: 저장 시 날짜 바뀌면 구 알림(id=date.hashCode()) 취소 후 enabled·미래면 재등록.
- ✅ **EditTodo/EditDate 추가 네비 배선**: OnAddTagClick→AddTag, OnAddRepeatCycleClick→AddRepeatCycle(빈 스텁 해소). EditTodo null 태그면 뒤로가기.
- ✅ **mondayStart 배선**: AddTodo/EditTodo/EditDate에 getMondayStart() 구독 + 날짜/반복 시트에 startFromMonday 전달.
- ✅ **미지정 태그 로케일**: tag_unassigned(ko/en/ja) 추가 + AddTodo/EditTodo 적용.
- ✅ **스낵바 호스트 배선**: 기존 미배선(TODO no-op)이던 Schedule/EditDate/AddTodo/EditTodo/Memo에 showSnackbar 전달 → 저장/검증/삭제 스낵바 실제 노출.
- ✅ **screen_view 애널리틱스**: RootContent activeChild→screen_view 중앙 로깅(전 화면). AddTodo/EditTodo Save Click + 넛지 NotificationNudge View 추가.
- ⬜ **잔여(의도적 스코프 아웃)**: 화면별 세부 버튼 Click 애널리틱스(Tag/RepeatCycle/Memo/Sync 등 다수) — 서피스 크고 buttonName 정합성 위험으로 별도. AddTodo/EditTodo/Home/Schedule 등 주요 Click·전 화면 screen_view는 완료.
- ⬜ **복귀 시 신규항목 자동선택(loadNewTag/loadNewRepeatCycle)**: AddTodo 포함 공용 미구현(Decompose child resume 훅 필요) — 별도.
