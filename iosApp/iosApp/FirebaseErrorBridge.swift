import Foundation
import FirebaseCore
import FirebaseCrashlytics
import FirebaseAnalytics
import FirebaseRemoteConfig

/// Crashlytics bridge for error reporting from Kotlin.
/// Only use when FirebaseApp is configured.
class FirebaseErrorBridge {
    static let shared = FirebaseErrorBridge()

    func logError(_ error: Error) {
        guard FirebaseApp.app() != nil else { return }
        Crashlytics.crashlytics().record(error: error)
    }

    func logError(message: String) {
        guard FirebaseApp.app() != nil else { return }
        let error = NSError(domain: "com.tgyuu.ebbingplanner", code: -1, userInfo: [
            NSLocalizedDescriptionKey: message,
        ])
        Crashlytics.crashlytics().record(error: error)
    }

    func setUserId(_ userId: String) {
        guard FirebaseApp.app() != nil else { return }
        Crashlytics.crashlytics().setUserID(userId)
        Analytics.setUserID(userId)
    }

    func clearUserId() {
        guard FirebaseApp.app() != nil else { return }
        Crashlytics.crashlytics().setUserID("")
        Analytics.setUserID(nil)
    }
}

/// Remote Config bridge for shared (IosRemoteConfigDataSource).
/// Android core/network ConfigDataSource·FeatureFlagDataSource 대응: fetchAndActivate 후 키 값을 문자열로 넘긴다.
/// Firebase 가 설정되지 않았으면 fetch 는 바로 끝나고 값은 빈 문자열(= shared 기본값 사용).
final class RemoteConfigBridge {
    static let shared = RemoteConfigBridge()

    private lazy var remoteConfig: RemoteConfig = {
        let config = RemoteConfig.remoteConfig()
        let settings = RemoteConfigSettings()
        // Android FeatureFlagDataSource 와 같은 최소 fetch 간격 (5분)
        settings.minimumFetchInterval = 300
        config.configSettings = settings
        return config
    }()

    func fetchAndActivate(completion: @escaping () -> Void) {
        guard FirebaseApp.app() != nil else {
            completion()
            return
        }
        // 실패해도 마지막으로 활성화된 값을 쓰도록 결과와 무관하게 completion 을 부른다
        remoteConfig.fetchAndActivate { _, _ in completion() }
    }

    func string(forKey key: String) -> String {
        guard FirebaseApp.app() != nil else { return "" }
        return remoteConfig.configValue(forKey: key).stringValue
    }
}
