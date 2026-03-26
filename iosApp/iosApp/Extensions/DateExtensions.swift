import Foundation
import shared

extension Date {
    /// Convert Swift Date to Kotlinx LocalDate
    func toKotlinxLocalDate() -> Kotlinx_datetimeLocalDate {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month, .day], from: self)
        return Kotlinx_datetimeLocalDate(
            year: Int32(components.year ?? 2024),
            monthNumber: Int32(components.month ?? 1),
            dayOfMonth: Int32(components.day ?? 1)
        )
    }

    /// Convert Swift Date to Kotlinx LocalDateTime
    func toKotlinxLocalDateTime() -> Kotlinx_datetimeLocalDateTime {
        let calendar = Calendar.current
        let components = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second, .nanosecond], from: self)
        return Kotlinx_datetimeLocalDateTime(
            year: Int32(components.year ?? 2024),
            monthNumber: Int32(components.month ?? 1),
            dayOfMonth: Int32(components.day ?? 1),
            hour: Int32(components.hour ?? 0),
            minute: Int32(components.minute ?? 0),
            second: Int32(components.second ?? 0),
            nanosecond: Int32(components.nanosecond ?? 0)
        )
    }

    /// Start of day for dictionary key comparison
    var startOfDay: Date {
        Calendar.current.startOfDay(for: self)
    }
}

extension Kotlinx_datetimeLocalDate {
    /// Convert Kotlinx LocalDate to Swift Date
    func toSwiftDate() -> Date {
        var components = DateComponents()
        components.year = Int(year)
        components.month = Int(monthNumber)
        components.day = Int(dayOfMonth)
        return Calendar.current.date(from: components) ?? Date()
    }
}

// MARK: - Helper functions for current date/time
func currentKotlinxLocalDate() -> Kotlinx_datetimeLocalDate {
    return Date().toKotlinxLocalDate()
}

func currentKotlinxLocalDateTime() -> Kotlinx_datetimeLocalDateTime {
    return Date().toKotlinxLocalDateTime()
}
