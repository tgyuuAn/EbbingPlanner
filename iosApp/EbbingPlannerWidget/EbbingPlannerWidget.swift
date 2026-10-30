import WidgetKit
import SwiftUI
import UIKit
import SQLite3
import AppIntents

// MARK: - Color helpers

extension Color {
    /// 0xRRGGBB 단색
    init(hex: UInt32, alpha: Double = 1.0) {
        self = Color(
            .sRGB,
            red: Double((hex >> 16) & 0xFF) / 255.0,
            green: Double((hex >> 8) & 0xFF) / 255.0,
            blue: Double(hex & 0xFF) / 255.0,
            opacity: alpha
        )
    }

    /// 라이트/다크 모드별 hex 색상 (Android Glance ColorProvider(day, night) 대응)
    init(hexLight: UInt32, hexDark: UInt32, alpha: Double = 1.0) {
        self = Color(uiColor: UIColor { trait in
            let hex = trait.userInterfaceStyle == .dark ? hexDark : hexLight
            return UIColor(
                red: CGFloat((hex >> 16) & 0xFF) / 255.0,
                green: CGFloat((hex >> 8) & 0xFF) / 255.0,
                blue: CGFloat(hex & 0xFF) / 255.0,
                alpha: CGFloat(alpha)
            )
        })
    }
}

// MARK: - Shared (앱과 공유하는 값)

enum WidgetShared {
    static let appGroup = "group.com.tgyuu.ebbingplanner"
    /// 앱의 onOpenURL 이 처리하는 딥링크 (Android 위젯 actionStartActivity 대응)
    static let scheme = "ebbingplanner"
    static let selectedDateKey = "widget_calendar_selected_date"

    /// 날짜 계산은 사용자 달력 설정(불기/일본력 등)과 무관하게 그레고리력으로 해요 (Android LocalDate 와 동일)
    static let calendar: Calendar = {
        var cal = Calendar(identifier: .gregorian)
        cal.locale = Locale(identifier: "en_US_POSIX")
        cal.timeZone = .current
        return cal
    }()

    static let dayFormatter: DateFormatter = {
        let f = DateFormatter()
        f.calendar = Calendar(identifier: .gregorian)
        f.locale = Locale(identifier: "en_US_POSIX")
        f.timeZone = .current
        f.dateFormat = "yyyy-MM-dd"
        return f
    }()

    static func dayString(_ date: Date) -> String { dayFormatter.string(from: date) }

    static func openAppURL() -> URL { URL(string: "\(scheme)://open")! }
    static func homeURL(date: String) -> URL { URL(string: "\(scheme)://home?date=\(date)")! }
    static func addTodoURL(date: String) -> URL { URL(string: "\(scheme)://addtodo?date=\(date)")! }

    /// 다음 자정 (날짜가 바뀌는 시점)
    static func nextMidnight(after date: Date) -> Date {
        let start = calendar.startOfDay(for: date)
        return calendar.date(byAdding: .day, value: 1, to: start) ?? date.addingTimeInterval(86_400)
    }

    /// 다음 갱신 시각: min(30분 뒤, 다음 자정)
    static func nextRefresh(after date: Date) -> Date {
        min(date.addingTimeInterval(30 * 60), nextMidnight(after: date))
    }

    /// 달력 위젯에서 고른 날짜. 이번 달이 아니면 오늘로 되돌려요 (Android SelectDateAction과 동일 규칙)
    static func calendarSelectedDate(now: Date = Date()) -> Date {
        guard let raw = UserDefaults(suiteName: appGroup)?.string(forKey: selectedDateKey),
              let date = dayFormatter.date(from: raw),
              calendar.isDate(date, equalTo: now, toGranularity: .month)
        else { return now }
        return date
    }

    static func setCalendarSelectedDate(_ date: String) {
        UserDefaults(suiteName: appGroup)?.set(date, forKey: selectedDateKey)
    }
}

// MARK: - Localization (Android core/designsystem strings.xml widget_* 미러링, ko/en/ja)

/// 위젯 타깃에 .strings 파일을 추가하지 않고(pbxproj 수정 회피) 언어별 사전으로 관리해요.
/// 지원하지 않는 언어는 Android 기본 values(한국어)와 같이 한국어로 폴백해요.
enum L10n {
    enum Lang { case ko, en, ja }

    static let lang: Lang = {
        for identifier in Locale.preferredLanguages {
            let code = identifier.lowercased()
            if code.hasPrefix("ko") { return .ko }
            if code.hasPrefix("en") { return .en }
            if code.hasPrefix("ja") { return .ja }
        }
        return .ko
    }()

    private static func pick(_ ko: String, _ en: String, _ ja: String) -> String {
        switch lang {
        case .ko: return ko
        case .en: return en
        case .ja: return ja
        }
    }

    static var locale: Locale {
        switch lang {
        case .ko: return Locale(identifier: "ko_KR")
        case .en: return Locale(identifier: "en_US")
        case .ja: return Locale(identifier: "ja_JP")
        }
    }

    // widget_today_todo / widget_today_todo_spaced
    static var todayTodo: String { pick("오늘 할 일", "Today's Tasks", "今日のタスク") }
    static var todayTodoSpaced: String { todayTodo + "   " }

    // widget_month_day_todo / widget_month_day_todo_spaced
    static func monthDayTodo(month: Int, day: Int) -> String {
        pick("\(month)월 \(day)일 할 일", "Tasks for \(month)/\(day)", "\(month)月\(day)日のタスク")
    }
    static func monthDayTodoSpaced(month: Int, day: Int) -> String { monthDayTodo(month: month, day: day) + "   " }

    // widget_empty_schedule
    static var emptySchedule: String { pick("오늘은 일정이 없어요", "No schedule for today", "今日の予定はありません") }

    // widget_year_month
    static func yearMonth(year: Int, month: Int) -> String {
        pick("\(year)년 \(month)월", "\(year). \(month)", "\(year)年\(month)月")
    }

    /// 요일 약칭 (Android DayOfWeek.toShortLabel = TextStyle.SHORT + 기본 Locale). 일요일부터.
    static var shortWeekdaySymbols: [String] {
        let f = DateFormatter()
        f.calendar = Calendar(identifier: .gregorian)
        f.locale = locale
        return f.shortWeekdaySymbols ?? ["S", "M", "T", "W", "T", "F", "S"]
    }

    // Android AndroidManifest receiver label
    static var todayWidgetName: String { pick("오늘 일정 위젯", "Today's Tasks Widget", "今日の予定ウィジェット") }
    static var calendarWidgetName: String { pick("이번 달 달력 위젯", "Monthly Calendar Widget", "今月のカレンダーウィジェット") }
    static var todayWidgetDescription: String {
        pick("오늘 해야 할 일정을 확인하세요.", "Check the tasks you need to do today.", "今日やるべき予定を確認できます。")
    }
    static var calendarWidgetDescription: String {
        pick("이번 달 일정을 달력으로 확인하세요.", "See this month's schedule on a calendar.", "今月の予定をカレンダーで確認できます。")
    }

    // AppIntent title 은 appintentsmetadataprocessor 가 문자열 리터럴만 허용해서 L10n 으로 바꿀 수 없어요.
    // (isDiscoverable = false 라 단축어/Spotlight 등 사용자에게 노출되지 않아요)
}

// MARK: - Widget Settings (앱 shared Settings.ios.kt 가 App Group 에 미러링한 값)

enum WidgetTheme: String {
    case NORMAL, FOREST, SUNSET, MARINE, LILAC
}

struct WidgetSettings {
    let theme: WidgetTheme
    let backgroundAlpha: Double
    let textAlpha: Double
    let sortByTag: Bool
    let mondayStart: Bool

    static let `default` = WidgetSettings(theme: .NORMAL, backgroundAlpha: 1, textAlpha: 1, sortByTag: false, mondayStart: false)

    /// 키 이름은 shared ConfigRepositoryImpl 과 동일해요.
    static func load() -> WidgetSettings {
        guard let d = UserDefaults(suiteName: WidgetShared.appGroup) else { return .default }
        let theme = WidgetTheme(rawValue: (d.string(forKey: "WIDGET_THEME") ?? "").uppercased()) ?? .NORMAL
        let bg = (d.object(forKey: "WIDGET_BACKGROUND_ALPHA") as? NSNumber)?.doubleValue ?? 1
        let text = (d.object(forKey: "WIDGET_TEXT_ALPHA") as? NSNumber)?.doubleValue ?? 1
        let sort = d.string(forKey: "SORT_TYPE") ?? "CREATED"
        let monday = (d.object(forKey: "MONDAY_START") as? NSNumber)?.boolValue ?? false
        return WidgetSettings(
            theme: theme,
            backgroundAlpha: snapBackgroundAlpha(bg),
            textAlpha: min(max(text, 0), 1),
            sortByTag: sort == "BY_TAG",
            mondayStart: monday
        )
    }

    /// Android 는 배경 투명도를 0.25/0.5/0.75/1 drawable 로 나눠요(그 외 값은 1).
    /// Android 는 0.5 를 25% drawable 로 잘못 매핑하고 있지만, iOS 는 의도대로 50% 로 그려요.
    private static func snapBackgroundAlpha(_ value: Double) -> Double {
        for step in [0.25, 0.5, 0.75] where abs(value - step) < 0.01 { return step }
        return 1
    }
}

// MARK: - Palette (Android designsystem EbbingColors light/dark × 5 테마)

struct WidgetPalette {
    let background: Color
    let textOnBackground: Color
    let textSub: Color
    let textDisabled: Color
    let textOnPrimary: Color
    let textPrimary: Color
    let fillTextfield: Color

    /// Android ic_widget_plus / ic_widget_return 은 테마와 무관한 고정 색이에요.
    static let iconFill = Color(hex: 0xF4F6FA)
    static let iconStroke = Color(hex: 0x5B5F72)

    private struct Scheme {
        let background, textOnBackground, textSub, textDisabled, textOnPrimary, textPrimary, fillTextfield: UInt32
    }

    private static func schemes(_ theme: WidgetTheme) -> (light: Scheme, dark: Scheme) {
        switch theme {
        case .NORMAL:
            return (
                Scheme(background: 0xFFFFFF, textOnBackground: 0x070808, textSub: 0x5B5F72, textDisabled: 0x8994A8,
                       textOnPrimary: 0xFFFFFF, textPrimary: 0x0F4C75, fillTextfield: 0xF4F6FA),
                Scheme(background: 0x070808, textOnBackground: 0xFFFFFF, textSub: 0xBCC6D4, textDisabled: 0x8994A8,
                       textOnPrimary: 0x070808, textPrimary: 0x82C0E2, fillTextfield: 0x131416)
            )
        case .FOREST:
            return (
                Scheme(background: 0xFBFEFB, textOnBackground: 0x0D120E, textSub: 0x2D6830, textDisabled: 0x8994A8,
                       textOnPrimary: 0xFBFEFB, textPrimary: 0x46A14B, fillTextfield: 0xF0F6F0),
                Scheme(background: 0x0D120E, textOnBackground: 0xFBFEFB, textSub: 0xC8EAC9, textDisabled: 0x8994A8,
                       textOnPrimary: 0x0D120E, textPrimary: 0x81C784, fillTextfield: 0x1E251E)
            )
        case .SUNSET:
            return (
                Scheme(background: 0xFFFCFA, textOnBackground: 0x181313, textSub: 0x944136, textDisabled: 0x8994A8,
                       textOnPrimary: 0xFFFCFA, textPrimary: 0xF4511E, fillTextfield: 0xFCEFE6),
                Scheme(background: 0x181313, textOnBackground: 0xFFFCFA, textSub: 0xF7D5C1, textDisabled: 0x8994A8,
                       textOnPrimary: 0x181313, textPrimary: 0xF77340, fillTextfield: 0x211918)
            )
        case .MARINE:
            return (
                Scheme(background: 0xF1F8FE, textOnBackground: 0x091F31, textSub: 0x0E5F96, textDisabled: 0x8994A8,
                       textOnPrimary: 0xF1F8FE, textPrimary: 0x1D95DA, fillTextfield: 0xCBE5F5),
                Scheme(background: 0x091F31, textOnBackground: 0xF1F8FE, textSub: 0xCBE5F5, textDisabled: 0x8994A8,
                       textOnPrimary: 0x091F31, textPrimary: 0x82C0E2, fillTextfield: 0x134467)
            )
        case .LILAC:
            return (
                Scheme(background: 0xFBFBFE, textOnBackground: 0x151418, textSub: 0x5E5779, textDisabled: 0x8994A8,
                       textOnPrimary: 0xFBFBFE, textPrimary: 0x8479DB, fillTextfield: 0xEEEFFC),
                Scheme(background: 0x151418, textOnBackground: 0xFBFBFE, textSub: 0xCFD2F6, textDisabled: 0x8994A8,
                       textOnPrimary: 0x151418, textPrimary: 0x9693E6, fillTextfield: 0x22212A)
            )
        }
    }

    /// Android EbbingWidgetTheme: 배경엔 backgroundAlpha, 글자/채움 색엔 textAlpha 를 적용해요.
    init(settings: WidgetSettings) {
        let s = Self.schemes(settings.theme)
        let a = settings.textAlpha
        background = Color(hexLight: s.light.background, hexDark: s.dark.background, alpha: settings.backgroundAlpha)
        textOnBackground = Color(hexLight: s.light.textOnBackground, hexDark: s.dark.textOnBackground, alpha: a)
        textSub = Color(hexLight: s.light.textSub, hexDark: s.dark.textSub, alpha: a)
        textDisabled = Color(hexLight: s.light.textDisabled, hexDark: s.dark.textDisabled, alpha: a)
        textOnPrimary = Color(hexLight: s.light.textOnPrimary, hexDark: s.dark.textOnPrimary, alpha: a)
        textPrimary = Color(hexLight: s.light.textPrimary, hexDark: s.dark.textPrimary, alpha: a)
        fillTextfield = Color(hexLight: s.light.fillTextfield, hexDark: s.dark.fillTextfield, alpha: a)
    }
}

// MARK: - Timeline Entry

struct TodoEntry: TimelineEntry {
    let date: Date
    let todos: [TodoItem]
    let settings: WidgetSettings
}

struct CalendarEntry: TimelineEntry {
    let date: Date
    let selectedDate: Date
    let colorsByDate: [String: [Color]]  // "yyyy-MM-dd" -> 해당 날짜 태그색(최대 3개, 색 0 제외)
    let todos: [TodoItem]                // 선택한 날짜의 할 일
    let settings: WidgetSettings
}

struct TodoItem: Identifiable {
    let id: Int
    let title: String
    let isDone: Bool
    let tagColor: Color
}

// MARK: - Database Reader

struct WidgetDatabaseReader {
    /// Android WidgetUpdater.sortComparator 와 같은 정렬
    /// CREATED: 상단 고정(priority) → 미완료 → 생성일 / BY_TAG: 태그명 → 상단 고정 → 미완료 → 생성일
    private static func orderClause(sortByTag: Bool) -> String {
        let base = "s.priority DESC, s.isDone ASC, s.createdAt ASC, s.id ASC"
        return sortByTag ? "t.name ASC, \(base)" : base
    }

    static func loadTodos(on day: String, sortByTag: Bool) -> [TodoItem] {
        guard let dbPath = getDatabasePath() else { return [] }

        var db: OpaquePointer?
        guard sqlite3_open_v2(dbPath, &db, SQLITE_OPEN_READONLY, nil) == SQLITE_OK else { return [] }
        defer { sqlite3_close(db) }

        // shared TodoSchedulesDao.loadTodoScheduleWithInfoAndTagByDate 와 같은 조건
        let query = """
            SELECT s.id, i.title, s.isDone, t.color
            FROM schedule s
            JOIN todo_info i ON s.infoId = i.id
            JOIN todo_tag t ON i.tagId = t.id
            WHERE s.date = ? AND s.isDeleted = 0 AND t.isDeleted = 0
            ORDER BY \(orderClause(sortByTag: sortByTag))
            LIMIT 50
            """

        var stmt: OpaquePointer?
        guard sqlite3_prepare_v2(db, query, -1, &stmt, nil) == SQLITE_OK else { return [] }
        defer { sqlite3_finalize(stmt) }

        sqlite3_bind_text(stmt, 1, (day as NSString).utf8String, -1, nil)

        var items: [TodoItem] = []
        while sqlite3_step(stmt) == SQLITE_ROW {
            let id = Int(sqlite3_column_int(stmt, 0))
            let title = sqlite3_column_text(stmt, 1).map { String(cString: $0) } ?? ""
            let isDone = sqlite3_column_int(stmt, 2) != 0
            let colorInt = Int(sqlite3_column_int64(stmt, 3))
            items.append(TodoItem(id: id, title: title, isDone: isDone, tagColor: color(from: colorInt)))
        }
        return items
    }

    /// 완료 토글 (Android TodoSchedulesDao.toggleDone 과 같은 쿼리, updatedAt 은 shared 컨버터 포맷)
    static func toggleDone(id: Int) {
        guard let dbPath = getDatabasePath() else { return }

        var db: OpaquePointer?
        guard sqlite3_open_v2(dbPath, &db, SQLITE_OPEN_READWRITE, nil) == SQLITE_OK else { return }
        defer { sqlite3_close(db) }
        sqlite3_busy_timeout(db, 2000)

        let query = "UPDATE schedule SET isDone = NOT isDone, updatedAt = ? WHERE id = ? AND isDeleted = 0"
        var stmt: OpaquePointer?
        guard sqlite3_prepare_v2(db, query, -1, &stmt, nil) == SQLITE_OK else { return }
        defer { sqlite3_finalize(stmt) }

        let f = DateFormatter()
        f.calendar = Calendar(identifier: .gregorian)
        f.locale = Locale(identifier: "en_US_POSIX")
        f.dateFormat = "yyyy-MM-dd HH:mm:ss"
        let now = f.string(from: Date())
        sqlite3_bind_text(stmt, 1, (now as NSString).utf8String, -1, nil)
        sqlite3_bind_int(stmt, 2, Int32(id))
        sqlite3_step(stmt)
    }

    private static func getDatabasePath() -> String? {
        let fileManager = FileManager.default
        guard let container = fileManager.containerURL(forSecurityApplicationGroupIdentifier: WidgetShared.appGroup) else {
            return nil
        }
        let dbPath = container.appendingPathComponent("ebbing-database").path // shared EbbingDatabase.NAME
        return fileManager.fileExists(atPath: dbPath) ? dbPath : nil
    }

    /// 이번 달 날짜별 태그 색상 ("yyyy-MM-dd" -> [Color])
    /// Android CalendarDayCell: 정렬된 일정의 color.distinct().take(3), 색 0(투명)은 그리지 않음
    static func loadMonthColorsByDate(for date: Date, sortByTag: Bool) -> [String: [Color]] {
        guard let dbPath = getDatabasePath() else { return [:] }
        var db: OpaquePointer?
        guard sqlite3_open_v2(dbPath, &db, SQLITE_OPEN_READONLY, nil) == SQLITE_OK else { return [:] }
        defer { sqlite3_close(db) }

        let cal = WidgetShared.calendar
        let comps = cal.dateComponents([.year, .month], from: date)
        guard let firstDay = cal.date(from: comps),
              let lastDay = cal.date(byAdding: DateComponents(month: 1, day: -1), to: firstDay) else {
            return [:]
        }

        let startStr = WidgetShared.dayString(firstDay)
        let endStr = WidgetShared.dayString(lastDay)

        let query = """
            SELECT s.date, t.color
            FROM schedule s
            JOIN todo_info i ON s.infoId = i.id
            JOIN todo_tag t ON i.tagId = t.id
            WHERE s.date >= ? AND s.date <= ? AND s.isDeleted = 0 AND t.isDeleted = 0
            ORDER BY s.date ASC, \(orderClause(sortByTag: sortByTag))
            """
        var stmt: OpaquePointer?
        guard sqlite3_prepare_v2(db, query, -1, &stmt, nil) == SQLITE_OK else { return [:] }
        defer { sqlite3_finalize(stmt) }

        sqlite3_bind_text(stmt, 1, (startStr as NSString).utf8String, -1, nil)
        sqlite3_bind_text(stmt, 2, (endStr as NSString).utf8String, -1, nil)

        var result: [String: [Int]] = [:]
        while sqlite3_step(stmt) == SQLITE_ROW {
            guard let c = sqlite3_column_text(stmt, 0) else { continue }
            let dateStr = String(cString: c)
            let colorInt = Int(sqlite3_column_int64(stmt, 1))
            var colors = result[dateStr] ?? []
            if !colors.contains(colorInt) { colors.append(colorInt) }  // distinct
            result[dateStr] = colors
        }
        return result.mapValues { ints in
            ints.prefix(3).filter { $0 != 0 }.map { color(from: $0) }
        }
    }

    /// ARGB Int → Color (Android Color(int) 와 동일하게 알파 포함, 0 이면 투명)
    static func color(from argb: Int) -> Color {
        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >> 8) & 0xFF) / 255.0
        let b = Double(argb & 0xFF) / 255.0
        return Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }
}

// MARK: - Layout helpers

enum WidgetLayout {
    /// Android TodoItemRow: 24dp 행 + 위아래 패딩
    static let rowHeight: CGFloat = 34

    /// 패밀리별 최대 행 수 (실제 표시 수는 남은 높이에 맞춰 더 줄어들 수 있어요)
    static func todayRowCap(_ family: WidgetFamily) -> Int {
        switch family {
        case .systemSmall: return 3
        case .systemMedium: return 4
        default: return 8
        }
    }

    static func calendarRowCap(_ family: WidgetFamily) -> Int {
        switch family {
        case .systemMedium: return 3
        default: return 8
        }
    }
}

extension View {
    /// iOS 17+ 는 시스템 content margin 을 쓰고, iOS 16 은 같은 크기로 직접 패딩해요.
    @ViewBuilder
    func widgetContentPadding() -> some View {
        if #available(iOS 17.0, *) {
            self
        } else {
            self.padding(16)
        }
    }

    @ViewBuilder
    func widgetBackground(_ color: Color) -> some View {
        if #available(iOS 17.0, *) {
            self.containerBackground(color, for: .widget)
        } else {
            self.background(color)
        }
    }
}

// MARK: - Shared Components

/// Android ic_widget_plus: 연회색 원 + 회색 플러스
struct PlusIcon: View {
    var size: CGFloat = 20

    var body: some View {
        ZStack {
            Circle().fill(WidgetPalette.iconFill)
            Image(systemName: "plus")
                .font(.system(size: size * 0.5, weight: .semibold))
                .foregroundColor(WidgetPalette.iconStroke)
        }
        .frame(width: size, height: size)
    }
}

/// Android RefreshAction (ic_widget_refresh, textSub 틴트). iOS 17+ 에서만 버튼으로 동작해요.
struct RefreshButton: View {
    let palette: WidgetPalette

    var body: some View {
        if #available(iOS 17.0, *) {
            Button(intent: RefreshWidgetIntent()) {
                Image(systemName: "arrow.clockwise")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(palette.textSub)
                    .frame(width: 20, height: 20)
            }
            .buttonStyle(.plain)
        }
    }
}

/// Android 진행도 헤더: "{title}{done}/{total}" (칩 배경 없음)
func progressText(title: String, done: Int, total: Int, doneColor: Color, palette: WidgetPalette) -> Text {
    Text(title).foregroundColor(palette.textOnBackground)
        + Text("\(done)").foregroundColor(doneColor)
        + Text("/\(total)").foregroundColor(palette.textDisabled)
}

/// 할일 행: [3×24 태그색 바 · 제목 · 원형 체크] (Android TodoItemRow)
/// 행을 누르면 해당 날짜가 선택된 홈으로(Android openHomeAction), 체크는 iOS 17+에서 바로 완료 토글(CheckTodoAction).
struct TodoRow: View {
    let todo: TodoItem
    let day: String
    let palette: WidgetPalette

    var body: some View {
        HStack(spacing: 0) {
            Link(destination: WidgetShared.homeURL(date: day)) {
                HStack(spacing: 0) {
                    Rectangle()
                        .fill(todo.tagColor)
                        .frame(width: 3, height: 24)

                    Text(todo.title)
                        .font(.system(size: 16, weight: todo.isDone ? .semibold : .medium))
                        .foregroundColor(todo.isDone ? palette.textDisabled : palette.textOnBackground)
                        .strikethrough(todo.isDone)
                        .lineLimit(1)
                        .padding(.horizontal, 8)
                        .frame(maxWidth: .infinity, alignment: .leading)
                }
            }

            if #available(iOS 17.0, *) {
                Button(intent: ToggleTodoIntent(todoId: todo.id)) { checkBox }
                    .buttonStyle(.plain)
            } else {
                checkBox
            }
        }
        .frame(height: WidgetLayout.rowHeight)
    }

    /// Android EbbingWidgetCheck: 체크 = 채워진 원 + 체크(ic_widget_check), 미체크 = 2dp 원 테두리
    @ViewBuilder
    private var checkBox: some View {
        if todo.isDone {
            Image(systemName: "checkmark.circle.fill")
                .resizable()
                .foregroundColor(todo.tagColor)
                .frame(width: 20, height: 20)
        } else {
            Circle()
                .stroke(todo.tagColor, lineWidth: 2)
                .frame(width: 18, height: 18)
                .frame(width: 20, height: 20)
        }
    }
}

/// 남은 높이에 들어가는 만큼만 행을 그려요 (Android LazyColumn 대응, 위젯은 스크롤 불가)
struct FittingTodoList: View {
    let todos: [TodoItem]
    let day: String
    let maxRows: Int
    let palette: WidgetPalette

    var body: some View {
        GeometryReader { geo in
            let fit = Int(geo.size.height / WidgetLayout.rowHeight)
            let count = max(0, min(maxRows, fit))
            VStack(alignment: .leading, spacing: 0) {
                ForEach(Array(todos.prefix(count))) { todo in
                    TodoRow(todo: todo, day: day, palette: palette)
                }
            }
            .frame(width: geo.size.width, height: geo.size.height, alignment: .topLeading)
        }
    }
}

struct EmptyScheduleText: View {
    let palette: WidgetPalette

    var body: some View {
        Text(L10n.emptySchedule)
            .font(.system(size: 16, weight: .semibold))
            .foregroundColor(palette.textSub)
            .lineLimit(2)
            .minimumScaleFactor(0.8)
            .frame(maxWidth: .infinity, alignment: .leading)
    }
}

// MARK: - Today Todo Timeline Provider

struct TodoTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> TodoEntry {
        TodoEntry(date: Date(), todos: [
            TodoItem(id: 1, title: L10n.todayTodo, isDone: false, tagColor: Color(hex: 0x3282B8)),
            TodoItem(id: 2, title: L10n.todayTodo, isDone: true, tagColor: Color(hex: 0xFF7490)),
        ], settings: .default)
    }

    private func makeEntry(at date: Date, settings: WidgetSettings) -> TodoEntry {
        TodoEntry(
            date: date,
            todos: WidgetDatabaseReader.loadTodos(on: WidgetShared.dayString(date), sortByTag: settings.sortByTag),
            settings: settings
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (TodoEntry) -> Void) {
        completion(makeEntry(at: Date(), settings: WidgetSettings.load()))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<TodoEntry>) -> Void) {
        let now = Date()
        let settings = WidgetSettings.load()
        // 갱신이 늦어져도 자정에는 다음 날 일정이 보이도록 자정 엔트리를 미리 넣어둬요
        let midnight = WidgetShared.nextMidnight(after: now)
        let entries = [makeEntry(at: now, settings: settings), makeEntry(at: midnight, settings: settings)]
        completion(Timeline(entries: entries, policy: .after(WidgetShared.nextRefresh(after: now))))
    }
}

// MARK: - Today Todo Widget View

struct TodayTodoWidgetView: View {
    var entry: TodoEntry
    @Environment(\.widgetFamily) var family

    private var palette: WidgetPalette { WidgetPalette(settings: entry.settings) }
    private var day: String { WidgetShared.dayString(entry.date) }

    var body: some View {
        let palette = self.palette
        VStack(alignment: .leading, spacing: 0) {
            header(palette)

            if entry.todos.isEmpty {
                EmptyScheduleText(palette: palette)
                    .padding(.vertical, 12)
                Spacer(minLength: 0)
            } else {
                FittingTodoList(
                    todos: entry.todos,
                    day: day,
                    maxRows: WidgetLayout.todayRowCap(family),
                    palette: palette
                )
                .padding(.top, 6)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .widgetContentPadding()
        .widgetURL(WidgetShared.openAppURL())
        .widgetBackground(palette.background)
    }

    private func header(_ palette: WidgetPalette) -> some View {
        let done = entry.todos.filter { $0.isDone }.count
        let total = entry.todos.count
        // Android: 완료가 1개 이상이거나 일정이 없으면 primary, 아니면 disabled
        let doneColor = (done > 0 || entry.todos.isEmpty) ? palette.textPrimary : palette.textDisabled
        return HStack(spacing: 0) {
            progressText(title: L10n.todayTodoSpaced, done: done, total: total, doneColor: doneColor, palette: palette)
                .font(.system(size: 18, weight: .bold))
                .lineLimit(1)
                .minimumScaleFactor(0.6)
            Spacer(minLength: 6)
            Link(destination: WidgetShared.addTodoURL(date: day)) {
                PlusIcon()
            }
            if #available(iOS 17.0, *) {
                Spacer().frame(width: family == .systemSmall ? 8 : 12)
                RefreshButton(palette: palette)
            }
        }
    }
}

// MARK: - Calendar Timeline Provider

struct CalendarTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> CalendarEntry {
        CalendarEntry(date: Date(), selectedDate: Date(), colorsByDate: [:], todos: [
            TodoItem(id: 1, title: L10n.todayTodo, isDone: false, tagColor: Color(hex: 0x3282B8)),
        ], settings: .default)
    }

    private func makeEntry(at date: Date, settings: WidgetSettings) -> CalendarEntry {
        let selected = WidgetShared.calendarSelectedDate(now: date)
        return CalendarEntry(
            date: date,
            selectedDate: selected,
            colorsByDate: WidgetDatabaseReader.loadMonthColorsByDate(for: date, sortByTag: settings.sortByTag),
            todos: WidgetDatabaseReader.loadTodos(on: WidgetShared.dayString(selected), sortByTag: settings.sortByTag),
            settings: settings
        )
    }

    func getSnapshot(in context: Context, completion: @escaping (CalendarEntry) -> Void) {
        completion(makeEntry(at: Date(), settings: WidgetSettings.load()))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<CalendarEntry>) -> Void) {
        let now = Date()
        let settings = WidgetSettings.load()
        let midnight = WidgetShared.nextMidnight(after: now)
        let entries = [makeEntry(at: now, settings: settings), makeEntry(at: midnight, settings: settings)]
        completion(Timeline(entries: entries, policy: .after(WidgetShared.nextRefresh(after: now))))
    }
}

// MARK: - Calendar Widget View

struct CalendarWidgetView: View {
    var entry: CalendarEntry
    @Environment(\.widgetFamily) var family

    private var cal: Calendar { WidgetShared.calendar }
    private var selectedDay: String { WidgetShared.dayString(entry.selectedDate) }
    private var todayDay: String { WidgetShared.dayString(entry.date) }

    var body: some View {
        let palette = WidgetPalette(settings: entry.settings)
        Group {
            switch family {
            case .systemSmall:
                calendarSection(palette, compact: true)
            case .systemMedium:
                HStack(alignment: .top, spacing: 14) {
                    calendarSection(palette, compact: true)
                        .frame(maxWidth: .infinity)
                    todoSection(palette, compact: true)
                        .frame(maxWidth: .infinity)
                }
            default:
                // Android 달력 위젯 세로 레이아웃: 달력 + 선택한 날짜 할 일
                VStack(alignment: .leading, spacing: 0) {
                    calendarSection(palette, compact: false)
                    todoSection(palette, compact: false)
                        .padding(.top, 10)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .widgetContentPadding()
        .widgetURL(WidgetShared.openAppURL())
        .widgetBackground(palette.background)
    }

    // MARK: Calendar section

    private func calendarSection(_ palette: WidgetPalette, compact: Bool) -> some View {
        let weeks = calendarWeeks
        return VStack(alignment: .leading, spacing: 0) {
            // 헤더: 년/월(왼쪽 정렬) · "+"(선택한 날짜로 일정 등록) · 오늘로 돌아가기
            HStack(spacing: 0) {
                Text(monthTitle)
                    .font(.system(size: compact ? 14 : 16, weight: .bold))
                    .foregroundColor(palette.textOnBackground)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
                Spacer(minLength: 4)
                Link(destination: WidgetShared.addTodoURL(date: selectedDay)) {
                    PlusIcon(size: compact ? 18 : 20)
                }
                Spacer().frame(width: compact ? 8 : 12)
                returnToTodayIcon
            }

            Spacer().frame(height: compact ? 4 : 8)

            // 요일 헤더 (textSub, medium) + 1pt 구분선
            HStack(spacing: 0) {
                ForEach(Array(weekdayLabels.enumerated()), id: \.offset) { _, label in
                    Text(label)
                        .font(.system(size: compact ? 10 : 14, weight: .medium))
                        .foregroundColor(palette.textSub)
                        .lineLimit(1)
                        .minimumScaleFactor(0.6)
                        .frame(maxWidth: .infinity)
                }
            }
            Spacer().frame(height: compact ? 1 : 2)
            Rectangle()
                .fill(palette.fillTextfield)
                .frame(height: 1)
            Spacer().frame(height: compact ? 1 : 4)

            if compact {
                // 남은 높이를 주 수로 나눠 셀 크기를 정해요
                GeometryReader { geo in
                    let cellHeight = geo.size.height / CGFloat(max(weeks.count, 1))
                    weekGrid(weeks, palette: palette, metrics: .compact(cellHeight: cellHeight))
                }
            } else {
                weekGrid(weeks, palette: palette, metrics: .regular)
            }
        }
    }

    private struct CellMetrics {
        let cellHeight: CGFloat
        let circle: CGFloat
        let fontSize: CGFloat
        let dotSize: CGFloat
        let showDots: Bool

        /// Android CalendarDayCell: 22dp 원 + 2dp + 7dp 점
        static let regular = CellMetrics(cellHeight: 31, circle: 22, fontSize: 12, dotSize: 7, showDots: true)

        static func compact(cellHeight: CGFloat) -> CellMetrics {
            let dot: CGFloat = 3
            let showDots = cellHeight >= 17
            let circle = min(22, max(10, cellHeight - (showDots ? dot + 1 : 0)))
            return CellMetrics(
                cellHeight: cellHeight,
                circle: circle,
                fontSize: min(12, max(9, circle * 0.62)),
                dotSize: dot,
                showDots: showDots
            )
        }
    }

    private func weekGrid(_ weeks: [[Date]], palette: WidgetPalette, metrics: CellMetrics) -> some View {
        VStack(spacing: 0) {
            ForEach(Array(weeks.enumerated()), id: \.offset) { _, week in
                HStack(spacing: 0) {
                    ForEach(Array(week.enumerated()), id: \.offset) { _, day in
                        dayCell(day, palette: palette, metrics: metrics)
                    }
                }
                .frame(height: metrics.cellHeight)
            }
        }
    }

    @ViewBuilder
    private var returnToTodayIcon: some View {
        let icon = Image(systemName: "arrow.uturn.backward")
            .font(.system(size: 12, weight: .semibold))
            .foregroundColor(WidgetPalette.iconStroke)
            .frame(width: 14, height: 14)
        if #available(iOS 17.0, *) {
            Button(intent: SelectCalendarDateIntent(date: todayDay)) { icon }
                .buttonStyle(.plain)
        } else {
            icon
        }
    }

    @ViewBuilder
    private func dayCell(_ day: Date, palette: WidgetPalette, metrics: CellMetrics) -> some View {
        // 날짜를 누르면 위젯 안에서 그 날짜를 선택해요 (Android SelectDateAction, 이번 달만)
        if #available(iOS 17.0, *) {
            Button(intent: SelectCalendarDateIntent(date: WidgetShared.dayString(day))) {
                dayCellContent(day, palette: palette, metrics: metrics)
            }
            .buttonStyle(.plain)
        } else {
            dayCellContent(day, palette: palette, metrics: metrics)
        }
    }

    private func dayCellContent(_ day: Date, palette: WidgetPalette, metrics: CellMetrics) -> some View {
        let dateStr = WidgetShared.dayString(day)
        let isToday = dateStr == todayDay
        let isSelected = dateStr == selectedDay
        let isCurrentMonth = cal.isDate(day, equalTo: entry.date, toGranularity: .month)
        let colors = entry.colorsByDate[dateStr] ?? []
        let textColor: Color = isCurrentMonth
            ? (isSelected ? palette.textOnPrimary : palette.textOnBackground)
            : palette.textDisabled

        return VStack(spacing: 0) {
            ZStack {
                if isSelected {
                    Circle().fill(palette.textOnBackground)
                }
                Text("\(cal.component(.day, from: day))")
                    .font(.system(size: metrics.fontSize, weight: isToday ? .bold : .medium))
                    .foregroundColor(textColor)
                    .lineLimit(1)
                    .minimumScaleFactor(0.7)
            }
            .frame(width: metrics.circle, height: metrics.circle)

            if metrics.showDots {
                HStack(spacing: metrics.dotSize > 4 ? 2 : 1) {
                    ForEach(Array(colors.enumerated()), id: \.offset) { _, c in
                        Circle().fill(c).frame(width: metrics.dotSize, height: metrics.dotSize)
                    }
                }
                .frame(height: metrics.dotSize)
                .padding(.top, metrics.dotSize > 4 ? 2 : 1)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .contentShape(Rectangle())
    }

    // MARK: Todo section

    private func todoSection(_ palette: WidgetPalette, compact: Bool) -> some View {
        let done = entry.todos.filter { $0.isDone }.count
        let total = entry.todos.count
        // Android SelectedDateTodoList: 완료 1개 이상이면 primary, 아니면 disabled
        let doneColor = done > 0 ? palette.textPrimary : palette.textDisabled
        return VStack(alignment: .leading, spacing: 0) {
            HStack(spacing: 0) {
                progressText(title: todoTitle, done: done, total: total, doneColor: doneColor, palette: palette)
                    .font(.system(size: 16, weight: .bold))
                    .lineLimit(1)
                    .minimumScaleFactor(0.6)
                Spacer(minLength: 6)
                RefreshButton(palette: palette)
            }
            Spacer().frame(height: compact ? 6 : 8)
            if entry.todos.isEmpty {
                EmptyScheduleText(palette: palette)
                Spacer(minLength: 0)
            } else {
                FittingTodoList(
                    todos: entry.todos,
                    day: selectedDay,
                    maxRows: WidgetLayout.calendarRowCap(family),
                    palette: palette
                )
            }
        }
    }

    /// Android widget_today_todo_spaced / widget_month_day_todo_spaced
    private var todoTitle: String {
        if selectedDay == todayDay { return L10n.todayTodoSpaced }
        return L10n.monthDayTodoSpaced(
            month: cal.component(.month, from: entry.selectedDate),
            day: cal.component(.day, from: entry.selectedDate)
        )
    }

    private var monthTitle: String {
        L10n.yearMonth(year: cal.component(.year, from: entry.date), month: cal.component(.month, from: entry.date))
    }

    /// 월요일 시작 설정을 반영한 요일 라벨
    private var weekdayLabels: [String] {
        let symbols = L10n.shortWeekdaySymbols  // 일요일부터
        guard symbols.count == 7 else { return symbols }
        return entry.settings.mondayStart ? Array(symbols[1...]) + [symbols[0]] : symbols
    }

    /// 이번 달에 필요한 주(4~6주)만 그려요. 앞뒤 인접 달 날짜 포함 (Android getCalendarDates + numberOfWeeks).
    private var calendarWeeks: [[Date]] {
        let comps = cal.dateComponents([.year, .month], from: entry.date)
        guard let firstDay = cal.date(from: comps),
              let daysInMonth = cal.range(of: .day, in: .month, for: firstDay)?.count else { return [] }
        let weekday = cal.component(.weekday, from: firstDay) // 1=일 … 7=토
        let leading = entry.settings.mondayStart ? (weekday + 5) % 7 : weekday - 1
        guard let gridStart = cal.date(byAdding: .day, value: -leading, to: firstDay) else { return [] }
        let weekCount = Int(ceil(Double(leading + daysInMonth) / 7.0))
        return (0..<weekCount).map { w in
            (0..<7).compactMap { d in cal.date(byAdding: .day, value: w * 7 + d, to: gridStart) }
        }
    }
}

// MARK: - Interactive Intents (iOS 17+)

/// Android CheckTodoAction: 위젯에서 바로 완료 토글
@available(iOS 17.0, *)
struct ToggleTodoIntent: AppIntent {
    static var title: LocalizedStringResource = "일정 완료 토글"
    static var isDiscoverable: Bool = false

    @Parameter(title: "Todo ID") var todoId: Int

    init() {}
    init(todoId: Int) { self.todoId = todoId }

    func perform() async throws -> some IntentResult {
        WidgetDatabaseReader.toggleDone(id: todoId)
        WidgetCenter.shared.reloadAllTimelines()
        return .result()
    }
}

/// Android SelectDateAction: 달력 위젯 안에서 날짜 선택
@available(iOS 17.0, *)
struct SelectCalendarDateIntent: AppIntent {
    static var title: LocalizedStringResource = "달력 날짜 선택"
    static var isDiscoverable: Bool = false

    @Parameter(title: "Date") var date: String

    init() {}
    init(date: String) { self.date = date }

    func perform() async throws -> some IntentResult {
        // 이번 달이 아니면 무시 (Android와 동일)
        if let picked = WidgetShared.dayFormatter.date(from: date),
           WidgetShared.calendar.isDate(picked, equalTo: Date(), toGranularity: .month) {
            WidgetShared.setCalendarSelectedDate(date)
        }
        WidgetCenter.shared.reloadTimelines(ofKind: "CalendarWidget")
        return .result()
    }
}

/// Android RefreshAction
@available(iOS 17.0, *)
struct RefreshWidgetIntent: AppIntent {
    static var title: LocalizedStringResource = "위젯 새로고침"
    static var isDiscoverable: Bool = false

    func perform() async throws -> some IntentResult {
        WidgetCenter.shared.reloadAllTimelines()
        return .result()
    }
}

// MARK: - Widget Configurations

struct CalendarWidget: Widget {
    let kind: String = "CalendarWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: CalendarTimelineProvider()) { entry in
            CalendarWidgetView(entry: entry)
        }
        .configurationDisplayName(L10n.calendarWidgetName)
        .description(L10n.calendarWidgetDescription)
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

struct TodayTodoWidget: Widget {
    let kind: String = "TodayTodoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: TodoTimelineProvider()) { entry in
            TodayTodoWidgetView(entry: entry)
        }
        .configurationDisplayName(L10n.todayWidgetName)
        .description(L10n.todayWidgetDescription)
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

@main
struct EbbingPlannerWidgetBundle: WidgetBundle {
    var body: some Widget {
        TodayTodoWidget()
        CalendarWidget()
    }
}
