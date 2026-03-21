import SwiftUI
import shared

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    @State private var selectedDate = Date()

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Calendar Header
                CalendarHeader(
                    currentDate: $viewModel.currentDisplayDate,
                    onPreviousMonth: { viewModel.previousMonth() },
                    onNextMonth: { viewModel.nextMonth() }
                )

                // Calendar Grid
                CalendarGrid(
                    currentDate: viewModel.currentDisplayDate,
                    selectedDate: $viewModel.selectedDate,
                    schedulesByDate: viewModel.schedulesByDate
                )
                .padding(.horizontal)

                Divider()
                    .padding(.vertical, 8)

                // Schedule List
                ScheduleListView(
                    schedules: viewModel.selectedDateSchedules,
                    onToggleDone: { schedule in
                        viewModel.toggleScheduleDone(schedule: schedule)
                    }
                )
            }
            .navigationTitle("에빙 플래너")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { viewModel.goToToday() }) {
                        Text("오늘")
                            .font(.subheadline)
                    }
                }
            }
        }
        .task {
            await viewModel.loadSchedules()
        }
    }
}

// MARK: - Calendar Header
struct CalendarHeader: View {
    @Binding var currentDate: Date
    let onPreviousMonth: () -> Void
    let onNextMonth: () -> Void

    private var monthYearString: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy년 M월"
        return formatter.string(from: currentDate)
    }

    var body: some View {
        HStack {
            Button(action: onPreviousMonth) {
                Image(systemName: "chevron.left")
                    .font(.title2)
                    .foregroundColor(.primary)
            }

            Spacer()

            Text(monthYearString)
                .font(.title2)
                .fontWeight(.semibold)

            Spacer()

            Button(action: onNextMonth) {
                Image(systemName: "chevron.right")
                    .font(.title2)
                    .foregroundColor(.primary)
            }
        }
        .padding()
    }
}

// MARK: - Calendar Grid
struct CalendarGrid: View {
    let currentDate: Date
    @Binding var selectedDate: Date
    let schedulesByDate: [Date: [TodoScheduleWrapper]]

    private let weekdays = ["일", "월", "화", "수", "목", "금", "토"]
    private let columns = Array(repeating: GridItem(.flexible()), count: 7)

    var body: some View {
        VStack(spacing: 8) {
            // Weekday headers
            LazyVGrid(columns: columns, spacing: 4) {
                ForEach(weekdays, id: \.self) { day in
                    Text(day)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }

            // Days
            LazyVGrid(columns: columns, spacing: 8) {
                ForEach(calendarDays(), id: \.self) { date in
                    CalendarDayCell(
                        date: date,
                        isSelected: Calendar.current.isDate(date, inSameDayAs: selectedDate),
                        isCurrentMonth: Calendar.current.isDate(date, equalTo: currentDate, toGranularity: .month),
                        isToday: Calendar.current.isDateInToday(date),
                        schedules: schedulesByDate[Calendar.current.startOfDay(for: date)] ?? []
                    )
                    .onTapGesture {
                        selectedDate = date
                    }
                }
            }
        }
    }

    private func calendarDays() -> [Date] {
        let calendar = Calendar.current

        guard let monthInterval = calendar.dateInterval(of: .month, for: currentDate),
              let monthFirstWeek = calendar.dateInterval(of: .weekOfMonth, for: monthInterval.start),
              let monthLastWeek = calendar.dateInterval(of: .weekOfMonth, for: monthInterval.end.addingTimeInterval(-1))
        else { return [] }

        var dates: [Date] = []
        var current = monthFirstWeek.start

        while current < monthLastWeek.end {
            dates.append(current)
            current = calendar.date(byAdding: .day, value: 1, to: current) ?? current
        }

        return dates
    }
}

// MARK: - Calendar Day Cell
struct CalendarDayCell: View {
    let date: Date
    let isSelected: Bool
    let isCurrentMonth: Bool
    let isToday: Bool
    let schedules: [TodoScheduleWrapper]

    private var dayNumber: String {
        let formatter = DateFormatter()
        formatter.dateFormat = "d"
        return formatter.string(from: date)
    }

    var body: some View {
        VStack(spacing: 4) {
            if isToday {
                Text("Today")
                    .font(.system(size: 8))
                    .foregroundColor(isSelected ? .white : .primary)
            } else {
                Text("")
                    .font(.system(size: 8))
            }

            Text(dayNumber)
                .font(.subheadline)
                .fontWeight(isToday ? .bold : .regular)
                .foregroundColor(textColor)

            // Schedule indicators
            HStack(spacing: 2) {
                ForEach(Array(Set(schedules.map { $0.color })).prefix(4), id: \.self) { color in
                    Circle()
                        .fill(Color(hex: color))
                        .frame(width: 6, height: 6)
                }
            }
            .frame(height: 6)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 4)
        .background(isSelected ? Color.black : Color.clear)
        .cornerRadius(8)
    }

    private var textColor: Color {
        if isSelected {
            return .white
        } else if !isCurrentMonth {
            return .secondary.opacity(0.5)
        } else {
            return .primary
        }
    }
}

// MARK: - Schedule List View
struct ScheduleListView: View {
    let schedules: [TodoScheduleWrapper]
    let onToggleDone: (TodoScheduleWrapper) -> Void

    var body: some View {
        if schedules.isEmpty {
            VStack {
                Spacer()
                Text("일정이 없습니다")
                    .foregroundColor(.secondary)
                Spacer()
            }
        } else {
            List {
                ForEach(schedules, id: \.id) { schedule in
                    ScheduleRowView(
                        schedule: schedule,
                        onToggleDone: { onToggleDone(schedule) }
                    )
                }
            }
            .listStyle(.plain)
        }
    }
}

// MARK: - Schedule Row View
struct ScheduleRowView: View {
    let schedule: TodoScheduleWrapper
    let onToggleDone: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            // Checkbox
            Button(action: onToggleDone) {
                Image(systemName: schedule.isDone ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(schedule.isDone ? .green : .secondary)
                    .font(.title2)
            }
            .buttonStyle(.plain)

            // Color indicator
            Circle()
                .fill(Color(hex: schedule.color))
                .frame(width: 12, height: 12)

            VStack(alignment: .leading, spacing: 4) {
                Text(schedule.title)
                    .font(.body)
                    .strikethrough(schedule.isDone)
                    .foregroundColor(schedule.isDone ? .secondary : .primary)

                Text(schedule.tagName)
                    .font(.caption)
                    .foregroundColor(.secondary)
            }

            Spacer()

            if schedule.priority > 0 {
                Text("P\(schedule.priority)")
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.orange.opacity(0.2))
                    .cornerRadius(4)
            }
        }
        .padding(.vertical, 4)
    }
}

#Preview {
    HomeView()
}
