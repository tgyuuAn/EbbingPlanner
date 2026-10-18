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
    @Environment(\.scenePhase) private var scenePhase

    /// 릴리스 빌드에서만 만들어진다. 디버그이거나 키가 없으면 nil → shared 가 콘솔 출력 헬퍼를 쓴다.
    private static var amplitudeBridge: AmplitudeAnalyticsBridge?

    init() {
        // Supabase 시크릿은 gitignore된 Secrets.plist에서 읽어 shared(initKoin)로 전달
        // (파일/값이 없으면 shared가 Stub sync로 폴백 — 크래시 없음)
        let secrets = Self.loadSecrets()
        let supabaseUrl = secrets["SUPABASE_URL"] ?? ""
        let supabaseKey = secrets["SUPABASE_ANON_KEY"] ?? ""

        Self.amplitudeBridge = Self.makeAmplitudeBridge(apiKey: secrets["AMPLITUDE_API_KEY"] ?? "")
        let onTrackAnalyticsEvent: ((String, [String: Any]) -> Void)? = Self.amplitudeBridge.map { bridge in
            { eventType, properties in bridge.track(eventType: eventType, properties: properties) }
        }
        let onSetAnalyticsUserId: ((String?) -> Void)? = Self.amplitudeBridge.map { bridge in
            { userId in bridge.setUserId(userId) }
        }

        // Check if Firebase is available
        if FirebaseApp.app() != nil {
            // Firebase is configured - use native implementations
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
                onTrackAnalyticsEvent: onTrackAnalyticsEvent,
                onSetAnalyticsUserId: onSetAnalyticsUserId,
                supabaseUrl: supabaseUrl,
                supabaseKey: supabaseKey
            )
        } else {
            // Firebase not configured - use debug/stub implementations
            IosModuleKt.doInitKoin(
                onTrackAnalyticsEvent: onTrackAnalyticsEvent,
                onSetAnalyticsUserId: onSetAnalyticsUserId,
                supabaseUrl: supabaseUrl,
                supabaseKey: supabaseKey
            )
        }
    }

    /// Android 는 BuildConfig.DEBUG 이면 Amplitude 대신 로그만 남긴다. iOS 도 디버그 빌드에서는 만들지 않는다.
    private static func makeAmplitudeBridge(apiKey: String) -> AmplitudeAnalyticsBridge? {
        #if DEBUG
        return nil
        #else
        let key = apiKey.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !key.isEmpty, !key.hasPrefix("YOUR_") else {
            print("⚠️ Amplitude: AMPLITUDE_API_KEY not configured. Analytics events will only be printed.")
            return nil
        }
        return AmplitudeAnalyticsBridge(apiKey: key)
        #endif
    }

    private static func loadSecrets() -> [String: String] {
        guard let path = Bundle.main.path(forResource: "Secrets", ofType: "plist"),
              let dict = NSDictionary(contentsOfFile: path) as? [String: Any] else {
            return [:]
        }
        var result: [String: String] = [:]
        for (key, value) in dict {
            if let stringValue = value as? String { result[key] = stringValue }
        }
        return result
    }

    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.all)
        }
        .onChange(of: scenePhase) { newPhase in
            switch newPhase {
            case .background:
                // 앱 백그라운드 진입 시 자동 백업 트리거 (조건은 shared에서 판단)
                AutoBackupBridgeKt.handleAppDidEnterBackground()
            case .active:
                // 포그라운드 복귀 시 실패한 백업 재시도
                AutoBackupBridgeKt.handleAppWillEnterForeground()
            default:
                break
            }
        }
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
