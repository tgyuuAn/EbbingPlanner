import Foundation
import shared

@MainActor
class HomeViewModel: ObservableObject {
    @Published var currentDisplayDate = Date()
    @Published var selectedDate = Date() {
        didSet {
            updateSelectedDateSchedules()
        }
    }
    @Published var schedulesByDate: [Date: [TodoScheduleWrapper]] = [:]
    @Published var selectedDateSchedules: [TodoScheduleWrapper] = []

    private let koinHelper = KoinHelper()

    func loadSchedules() async {
        do {
            let calendar = Calendar.current

            // Get month range
            guard let monthInterval = calendar.dateInterval(of: .month, for: currentDisplayDate) else { return }

            let startDate = monthInterval.start.toKotlinxLocalDate()
            let endDate = monthInterval.end.toKotlinxLocalDate()

            // Load schedules from Room via Koin
            let schedules = try await koinHelper.schedulesDao.loadTodoSchedulesByDateRange(
                startDate: startDate,
                endDate: endDate
            )

            // Group by date
            var grouped: [Date: [TodoScheduleWrapper]] = [:]
            for schedule in schedules {
                let wrapper = TodoScheduleWrapper(from: schedule)
                let date = wrapper.date.startOfDay
                if grouped[date] != nil {
                    grouped[date]?.append(wrapper)
                } else {
                    grouped[date] = [wrapper]
                }
            }

            schedulesByDate = grouped
            updateSelectedDateSchedules()
        } catch {
            print("Failed to load schedules: \(error)")
        }
    }

    func toggleScheduleDone(schedule: TodoScheduleWrapper) {
        Task {
            do {
                let now = currentKotlinxLocalDateTime()
                // Update in database
                try await koinHelper.todoWithSchedulesDao.updateSchedule(
                    id: schedule.id,
                    date: schedule.kotlinxDate,
                    memo: schedule.memo,
                    priority: schedule.priority,
                    isDone: !schedule.isDone,
                    updatedAt: now
                )

                // Reload schedules
                await loadSchedules()
            } catch {
                print("Failed to toggle schedule: \(error)")
            }
        }
    }

    func previousMonth() {
        if let newDate = Calendar.current.date(byAdding: .month, value: -1, to: currentDisplayDate) {
            currentDisplayDate = newDate
            Task {
                await loadSchedules()
            }
        }
    }

    func nextMonth() {
        if let newDate = Calendar.current.date(byAdding: .month, value: 1, to: currentDisplayDate) {
            currentDisplayDate = newDate
            Task {
                await loadSchedules()
            }
        }
    }

    func goToToday() {
        currentDisplayDate = Date()
        selectedDate = Date()
        Task {
            await loadSchedules()
        }
    }

    private func updateSelectedDateSchedules() {
        let key = Calendar.current.startOfDay(for: selectedDate)
        selectedDateSchedules = schedulesByDate[key] ?? []
    }
}

// MARK: - TodoSchedule Wrapper for Swift
struct TodoScheduleWrapper: Identifiable, Hashable {
    let id: Int32
    let infoId: Int32
    let title: String
    let tagId: Int32
    let tagName: String
    let color: Int32
    let date: Date
    let kotlinxDate: Kotlinx_datetimeLocalDate
    let memo: String
    let priority: Int32
    let isDone: Bool

    init(from schedule: TodoSchedule) {
        self.id = schedule.id
        self.infoId = schedule.infoId
        self.title = schedule.title
        self.tagId = schedule.tagId
        self.tagName = schedule.name
        self.color = schedule.color
        self.kotlinxDate = schedule.date
        self.date = schedule.date.toSwiftDate()
        self.memo = schedule.memo
        self.priority = schedule.priority
        self.isDone = schedule.isDone
    }

    func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }

    static func == (lhs: TodoScheduleWrapper, rhs: TodoScheduleWrapper) -> Bool {
        lhs.id == rhs.id
    }
}
