import SwiftUI

extension Color {
    /// Initialize Color from Int32 hex value (e.g., 0xFFBBE1FA)
    init(hex: Int32) {
        let uInt = UInt32(bitPattern: hex)
        let red = Double((uInt >> 16) & 0xFF) / 255.0
        let green = Double((uInt >> 8) & 0xFF) / 255.0
        let blue = Double(uInt & 0xFF) / 255.0
        let alpha = Double((uInt >> 24) & 0xFF) / 255.0

        self.init(.sRGB, red: red, green: green, blue: blue, opacity: alpha)
    }

    /// App theme colors
    static let ebbingPrimary = Color.black
    static let ebbingBackground = Color(UIColor.systemBackground)
    static let ebbingSecondary = Color(UIColor.secondarySystemBackground)
}
