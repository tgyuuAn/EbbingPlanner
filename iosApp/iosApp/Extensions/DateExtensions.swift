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
