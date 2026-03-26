import SwiftUI
import shared

@main
struct EbbingPlannerApp: App {
    init() {
        // Initialize Koin
        IosModuleKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
