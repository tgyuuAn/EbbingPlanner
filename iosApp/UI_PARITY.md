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
