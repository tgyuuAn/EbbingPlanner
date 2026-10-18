import Foundation
import FirebaseCore
import FirebaseCrashlytics
import FirebaseAnalytics

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
