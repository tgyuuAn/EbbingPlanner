import SwiftUI
import shared
import FirebaseCore

class AppDelegate: NSObject, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        // Only configure Firebase if GoogleService-Info.plist has real values
        if isFirebaseConfigured() {
            FirebaseApp.configure()
        } else {
            print("⚠️ Firebase: GoogleService-Info.plist not configured. Skipping Firebase initialization.")
        }
        return true
    }

    private func isFirebaseConfigured() -> Bool {
        guard let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
              let dict = NSDictionary(contentsOfFile: path),
              let apiKey = dict["API_KEY"] as? String else {
            return false
        }
        return !apiKey.hasPrefix("TODO_")
    }
}

@main
struct EbbingPlannerApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    init() {
        // Check if Firebase is available
        if FirebaseApp.app() != nil {
            // Firebase is configured - use native implementations
            initKoinWithFirebaseIntegration()
        } else {
            // Firebase not configured - use debug/stub implementations
            IosModuleKt.doInitKoin()
        }
    }

    private func initKoinWithFirebaseIntegration() {
        IosModuleKt.doInitKoinWithFirebase(
            onLogError: { message in
                FirebaseErrorBridge.shared.logError(message: message)
            },
            onSetErrorUserId: { userId in
                FirebaseErrorBridge.shared.setUserId(userId)
            },
            onClearErrorUserId: {
                FirebaseErrorBridge.shared.clearUserId()
            },
            onLogAnalyticsEvent: { name, params in
                FirebaseAnalyticsBridge.shared.logEvent(name: name, parameters: params as? [String: Any])
            },
            onSetAnalyticsUserId: { userId in
                FirebaseAnalyticsBridge.shared.setUserId(userId)
            }
        )
    }

    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.all)
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
