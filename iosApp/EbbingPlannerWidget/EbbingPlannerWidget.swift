import WidgetKit
import SwiftUI
import SQLite3

// MARK: - Timeline Entry

struct TodoEntry: TimelineEntry {
    let date: Date
    let todos: [TodoItem]
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
            LIMIT 6
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

    private static func formattedToday() -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        return formatter.string(from: Date())
    }

    private static func color(from argb: Int) -> Color {
        let a = Double((argb >> 24) & 0xFF) / 255.0
        let r = Double((argb >> 16) & 0xFF) / 255.0
        let g = Double((argb >> 8) & 0xFF) / 255.0
        let b = Double(argb & 0xFF) / 255.0
        return Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }
}

// MARK: - Timeline Provider

struct TodoTimelineProvider: TimelineProvider {
    func placeholder(in context: Context) -> TodoEntry {
        TodoEntry(date: Date(), todos: [
            TodoItem(id: 1, title: "오늘의 할일", isDone: false, tagColor: .blue),
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

// MARK: - Widget View

struct TodayTodoWidgetView: View {
    var entry: TodoEntry

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(formattedDate)
                    .font(.caption)
                    .foregroundColor(.secondary)
                Spacer()
                Text("에빙 플래너")
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }

            if entry.todos.isEmpty {
                Spacer()
                Text("오늘의 할일이 없습니다")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                Spacer()
            } else {
                ForEach(entry.todos.prefix(5)) { todo in
                    HStack(spacing: 6) {
                        Circle()
                            .fill(todo.tagColor)
                            .frame(width: 8, height: 8)

                        Image(systemName: todo.isDone ? "checkmark.circle.fill" : "circle")
                            .font(.caption)
                            .foregroundColor(todo.isDone ? .green : .gray)

                        Text(todo.title)
                            .font(.caption)
                            .lineLimit(1)
                            .strikethrough(todo.isDone)
                            .foregroundColor(todo.isDone ? .secondary : .primary)
                    }
                }
            }

            Spacer(minLength: 0)
        }
        .padding()
    }

    private var formattedDate: String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "ko_KR")
        formatter.dateFormat = "M월 d일 (E)"
        return formatter.string(from: entry.date)
    }
}

// MARK: - Widget Configuration

struct TodayTodoWidget: Widget {
    let kind: String = "TodayTodoWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: TodoTimelineProvider()) { entry in
            if #available(iOS 17.0, *) {
                TodayTodoWidgetView(entry: entry)
                    .containerBackground(.fill.tertiary, for: .widget)
            } else {
                TodayTodoWidgetView(entry: entry)
                    .padding()
                    .background(Color(.systemBackground))
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
    }
}
