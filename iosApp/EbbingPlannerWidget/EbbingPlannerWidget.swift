import WidgetKit
import SwiftUI
import UIKit
import SQLite3

// MARK: - Design System (Android EbbingWidget 토큰 미러링)

extension Color {
    /// 라이트/다크 모드별 hex 색상. Android designsystem Color.kt 값과 동일.
    init(hexLight: UInt32, hexDark: UInt32) {
        self = Color(uiColor: UIColor { trait in
            let hex = trait.userInterfaceStyle == .dark ? hexDark : hexLight
            return UIColor(
                red: CGFloat((hex >> 16) & 0xFF) / 255.0,
                green: CGFloat((hex >> 8) & 0xFF) / 255.0,
                blue: CGFloat(hex & 0xFF) / 255.0,
                alpha: 1.0
            )
        })
    }
}

enum EWColor {
    static let background = Color(hexLight: 0xF4F6FA, hexDark: 0x070808)
    static let surface = Color(hexLight: 0x070808, hexDark: 0xFFFFFF)         // 기본 텍스트
    static let primary = Color(hexLight: 0x0F4C75, hexDark: 0x82C0E2)         // 강조(완료수 등)
    static let headerBackground = Color(hexLight: 0xA1AABB, hexDark: 0x4B4F5D) // 헤더 칩 배경(onSurfaceVariant)
    static let tertiary = Color(hexLight: 0x8994A8, hexDark: 0x8994A8)        // 다른 달 날짜
    static let inverseSurface = Color(hexLight: 0xFFFFFF, hexDark: 0x070808)  // 선택 셀 텍스트
}

enum EWFont {
    static let heading14B = Font.system(size: 14, weight: .bold)
    static let body14M = Font.system(size: 14, weight: .medium)
    static let caption12R = Font.system(size: 12, weight: .regular)
    static let caption12B = Font.system(size: 12, weight: .bold)
}

// MARK: - Timeline Entry

struct TodoEntry: TimelineEntry {
    let date: Date
    let todos: [TodoItem]
}

struct CalendarEntry: TimelineEntry {
    let date: Date
    let colorsByDate: [String: [Color]]  // "yyyy-MM-dd" -> 해당 날짜 태그색들
    let todayTodos: [TodoItem]
}

struct TodoItem: Identifiable {
    let id: Int
    let title: String
    let isDone: Bool
    let tagColor: Color
}

// MARK: - Database Reader

struct WidgetDatabaseReader {
    static func loadTodayTodos() -> [TodoItem] {
        guard let dbPath = getDatabasePath() else { return [] }

        var db: OpaquePointer?
        guard sqlite3_open_v2(dbPath, &db, SQLITE_OPEN_READONLY, nil) == SQLITE_OK else { return [] }
        defer { sqlite3_close(db) }

        let today = formattedToday()
        let query = """
            SELECT s.id, i.title, s.isDone, COALESCE(t.color, 0)
            FROM schedule s
            INNER JOIN todo_info i ON s.infoId = i.id
            LEFT JOIN todo_tag t ON i.tagId = t.id
            WHERE s.date = ? AND s.isDeleted = 0
            ORDER BY s.isDone ASC, s.createdAt ASC
            LIMIT 50
            """

        var stmt: OpaquePointer?
        guard sqlite3_prepare_v2(db, query, -1, &stmt, nil) == SQLITE_OK else { return [] }
        defer { sqlite3_finalize(stmt) }

        sqlite3_bind_text(stmt, 1, (today as NSString).utf8String, -1, nil)

        var items: [TodoItem] = []
        while sqlite3_step(stmt) == SQLITE_ROW {
            let id = Int(sqlite3_column_int(stmt, 0))
            let title = String(cString: sqlite3_column_text(stmt, 1))
            let isDone = sqlite3_column_int(stmt, 2) != 0
            let colorInt = Int(sqlite3_column_int64(stmt, 3))
            items.append(TodoItem(id: id, title: title, isDone: isDone, tagColor: color(from: colorInt)))
        }
        return items
    }

    private static func getDatabasePath() -> String? {
        let fileManager = FileManager.default
        guard let container = fileManager.containerURL(forSecurityApplicationGroupIdentifier: "group.com.tgyuu.ebbingplanner") else {
            return nil
        }
        let dbPath = container.appendingPathComponent("ebbingdatabase").path
        return fileManager.fileExists(atPath: dbPath) ? dbPath : nil
    }

    /// 이번 달 날짜별 태그 색상 목록 ("yyyy-MM-dd" -> [Color])
    static func loadMonthColorsByDate(for date: Date) -> [String: [Color]] {
        guard let dbPath = getDatabasePath() else { return [:] }
        var db: OpaquePointer?
        guard sqlite3_open_v2(dbPath, &db, SQLITE_OPEN_READONLY, nil) == SQLITE_OK else { return [:] }
        defer { sqlite3_close(db) }

        let cal = Calendar.current
        let comps = cal.dateComponents([.year, .month], from: date)
        guard let firstDay = cal.date(from: comps),
              let lastDay = cal.date(byAdding: DateComponents(month: 1, day: -1), to: firstDay) else {
            return [:]
        }

        let fmt = DateFormatter()
        fmt.dateFormat = "yyyy-MM-dd"
        let startStr = fmt.string(from: firstDay)
        let endStr = fmt.string(from: lastDay)

        let query = """
            SELECT s.date, COALESCE(t.color, 0)
            FROM schedule s
            INNER JOIN todo_info i ON s.infoId = i.id
            LEFT JOIN todo_tag t ON i.tagId = t.id
            WHERE s.date >= ? AND s.date <= ? AND s.isDeleted = 0
            ORDER BY s.createdAt ASC
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
            if !colors.contains(colorInt) { colors.append(colorInt) }  // 중복 제거(distinct)
            result[dateStr] = colors
        }
        return result.mapValues { ints in ints.prefix(4).map { color(from: $0) } }  // 최대 4개
    }

    private static func formattedToday() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }

    static func color(from argb: Int) -> Color {
        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >> 8) & 0xFF) / 255.0
        let b = Double(argb & 0xFF) / 255.0
        // 태그 없음(0) 등 투명/무색은 강조색으로 대체
        if a == 0 { return EWColor.primary }
        return Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }
}

// MARK: - Today Todo Timeline Provider

struct TodoTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> TodoEntry {
        TodoEntry(date: Date(), todos: [
            TodoItem(id: 1, title: "오늘의 할일", isDone: false, tagColor: EWColor.primary),
            TodoItem(id: 2, title: "복습하기", isDone: true, tagColor: .green),
        ])
    }

    func getSnapshot(in context: Context, completion: @escaping (TodoEntry) -> Void) {
        let todos = WidgetDatabaseReader.loadTodayTodos()
        completion(TodoEntry(date: Date(), todos: todos))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<TodoEntry>) -> Void) {
        let todos = WidgetDatabaseReader.loadTodayTodos()
        let entry = TodoEntry(date: Date(), todos: todos)
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: Date())!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
}

// MARK: - Shared Components

/// 진행도 헤더 칩: "{prefix} {done} /{total}" (Android TodayTodo 헤더와 동일)
struct ProgressHeader: View {
    let title: String
    let doneCount: Int
    let totalCount: Int

    var body: some View {
        HStack(spacing: 0) {
            Text(title)
                .font(EWFont.heading14B)
                .foregroundColor(EWColor.surface)
            Text("\(doneCount)")
                .font(EWFont.heading14B)
                .foregroundColor(EWColor.primary)
            Text(" /\(totalCount)")
                .font(EWFont.heading14B)
                .foregroundColor(EWColor.surface)
            Spacer(minLength: 4)
            Image(systemName: "plus")
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(EWColor.surface)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 6)
        .background(EWColor.headerBackground)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

/// 할일 행: [색 dot · 제목 · 체크박스] (Android TodoItemRow와 동일)
struct TodoRow: View {
    let todo: TodoItem

    var body: some View {
        HStack(spacing: 8) {
            Circle()
                .fill(todo.tagColor)
                .frame(width: 16, height: 16)

            Text(todo.title)
                .font(.system(size: 14, weight: todo.isDone ? .bold : .regular))
                .foregroundColor(EWColor.surface)
                .strikethrough(todo.isDone)
                .lineLimit(2)

            Spacer(minLength: 4)

            RoundedRectangle(cornerRadius: 6)
                .stroke(todo.tagColor, lineWidth: 1.5)
                .frame(width: 20, height: 20)
                .overlay(
                    todo.isDone
                        ? Image(systemName: "checkmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(todo.tagColor)
                        : nil
                )
        }
    }
}

struct EmptyScheduleText: View {
    var body: some View {
        Text("금일 스케줄이 없어요.")
            .font(EWFont.body14M)
            .foregroundColor(EWColor.surface)
            .frame(maxWidth: .infinity, alignment: .center)
    }
}

// MARK: - Today Todo Widget View

struct TodayTodoWidgetView: View {
    var entry: TodoEntry

    private var doneCount: Int { entry.todos.filter { $0.isDone }.count }
    private var totalCount: Int { entry.todos.count }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ProgressHeader(title: "오늘 할 일   ", doneCount: doneCount, totalCount: totalCount)

            if entry.todos.isEmpty {
                Spacer(minLength: 0)
                EmptyScheduleText()
                Spacer(minLength: 0)
            } else {
                VStack(alignment: .leading, spacing: 6) {
                    ForEach(entry.todos.prefix(5)) { todo in
                        TodoRow(todo: todo)
                    }
                }
                Spacer(minLength: 0)
            }
        }
        .padding(12)
    }
}

// MARK: - Calendar Timeline Provider

struct CalendarTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> CalendarEntry {
        CalendarEntry(date: Date(), colorsByDate: [:], todayTodos: [
            TodoItem(id: 1, title: "오늘의 할일", isDone: false, tagColor: EWColor.primary),
        ])
    }

    func getSnapshot(in context: Context, completion: @escaping (CalendarEntry) -> Void) {
        let colors = WidgetDatabaseReader.loadMonthColorsByDate(for: Date())
        let todos = WidgetDatabaseReader.loadTodayTodos()
        completion(CalendarEntry(date: Date(), colorsByDate: colors, todayTodos: todos))
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<CalendarEntry>) -> Void) {
        let colors = WidgetDatabaseReader.loadMonthColorsByDate(for: Date())
        let todos = WidgetDatabaseReader.loadTodayTodos()
        let entry = CalendarEntry(date: Date(), colorsByDate: colors, todayTodos: todos)
        let nextUpdate = Calendar.current.date(byAdding: .minute, value: 30, to: Date())!
        completion(Timeline(entries: [entry], policy: .after(nextUpdate)))
    }
}

// MARK: - Calendar Widget View

struct CalendarWidgetView: View {
    var entry: CalendarEntry
    @Environment(\.widgetFamily) var family

    private let dayOfWeekLabels = ["일", "월", "화", "수", "목", "금", "토"]
    private let dateFmt: DateFormatter = {
        let f = DateFormatter()
        f.dateFormat = "yyyy-MM-dd"
        return f
    }()

    var body: some View {
        if family == .systemMedium {
            HStack(alignment: .top, spacing: 12) {
                calendarSection
                todoSection
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            .padding(8)
        } else {
            calendarSection
                .padding(8)
        }
    }

    private var calendarSection: some View {
        VStack(alignment: .leading, spacing: 4) {
            // 헤더: 월/년 + 오늘로 돌아가기 아이콘
            HStack {
                Spacer().frame(width: 14)
                Text(monthTitle)
                    .font(EWFont.heading14B)
                    .foregroundColor(EWColor.surface)
                    .frame(maxWidth: .infinity)
                Image(systemName: "arrow.uturn.left")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(EWColor.surface)
                    .frame(width: 14)
            }

            // 요일 헤더
            HStack(spacing: 0) {
                ForEach(dayOfWeekLabels, id: \.self) { label in
                    Text(label)
                        .font(EWFont.caption12B)
                        .foregroundColor(EWColor.surface)
                        .frame(maxWidth: .infinity)
                }
            }

            // 6주 그리드
            LazyVGrid(columns: Array(repeating: GridItem(.flexible(), spacing: 0), count: 7), spacing: 2) {
                ForEach(Array(calendarCells.enumerated()), id: \.offset) { _, day in
                    dayCell(day)
                }
            }

            Spacer(minLength: 0)
        }
    }

    private func dayCell(_ day: Date) -> some View {
        let cal = Calendar.current
        let dateStr = dateFmt.string(from: day)
        let isToday = cal.isDateInToday(day)
        let isCurrentMonth = cal.isDate(day, equalTo: entry.date, toGranularity: .month)
        let colors = entry.colorsByDate[dateStr] ?? []

        return ZStack {
            if isToday {
                RoundedRectangle(cornerRadius: 8)
                    .fill(EWColor.surface)
            }
            VStack(spacing: 2) {
                Text("\(cal.component(.day, from: day))")
                    .font(.system(size: 12, weight: isToday ? .bold : .regular))
                    .foregroundColor(
                        isToday ? EWColor.inverseSurface
                            : (isCurrentMonth ? EWColor.surface : EWColor.tertiary)
                    )
                HStack(spacing: 2) {
                    ForEach(Array(colors.prefix(4).enumerated()), id: \.offset) { _, c in
                        Circle().fill(c).frame(width: 6, height: 6)
                    }
                }
                .frame(height: 6)
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: 34)
    }

    private var todoSection: some View {
        let done = entry.todayTodos.filter { $0.isDone }.count
        let total = entry.todayTodos.count
        return VStack(alignment: .leading, spacing: 8) {
            ProgressHeader(title: "오늘 할 일   ", doneCount: done, totalCount: total)
            if entry.todayTodos.isEmpty {
                Spacer(minLength: 0)
                EmptyScheduleText()
                Spacer(minLength: 0)
            } else {
                ForEach(entry.todayTodos.prefix(3)) { todo in
                    TodoRow(todo: todo)
                }
                Spacer(minLength: 0)
            }
        }
    }

    private var monthTitle: String {
        let f = DateFormatter()
        f.locale = Locale(identifier: "ko_KR")
        f.dateFormat = "yyyy년 M월"
        return f.string(from: entry.date)
    }

    /// 6주(42칸) 전체 그리드. 앞뒤 인접 달 날짜 포함(Android와 동일).
    private var calendarCells: [Date] {
        let cal = Calendar.current
        let comps = cal.dateComponents([.year, .month], from: entry.date)
        guard let firstDay = cal.date(from: comps) else { return [] }
        let leading = cal.component(.weekday, from: firstDay) - 1 // 0=일
        guard let gridStart = cal.date(byAdding: .day, value: -leading, to: firstDay) else { return [] }
        return (0..<42).compactMap { cal.date(byAdding: .day, value: $0, to: gridStart) }
    }
}

// MARK: - Widget Configurations

struct CalendarWidget: Widget {
    let kind: String = "CalendarWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: CalendarTimelineProvider()) { entry in
            if #available(iOS 17.0, *) {
                CalendarWidgetView(entry: entry)
                    .containerBackground(EWColor.background, for: .widget)
            } else {
                CalendarWidgetView(entry: entry)
                    .background(EWColor.background)
            }
        }
        .configurationDisplayName("달력")
        .description("이번 달 일정을 달력으로 확인하세요.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

struct TodayTodoWidget: Widget {
    let kind: String = "TodayTodoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: TodoTimelineProvider()) { entry in
            if #available(iOS 17.0, *) {
                TodayTodoWidgetView(entry: entry)
                    .containerBackground(EWColor.background, for: .widget)
            } else {
                TodayTodoWidgetView(entry: entry)
                    .background(EWColor.background)
            }
        }
        .configurationDisplayName("오늘의 할일")
        .description("오늘 해야 할 일정을 확인하세요.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}

@main
struct EbbingPlannerWidgetBundle: WidgetBundle {
    var body: some Widget {
        TodayTodoWidget()
        CalendarWidget()
    }
}
