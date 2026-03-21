import SwiftUI
import shared

@main
struct EbbingPlannerApp: App {
    init() {
        // Initialize Koin
        IosModuleKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
