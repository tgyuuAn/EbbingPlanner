import Foundation
import AmplitudeSwift

/// Amplitude bridge for product analytics from Kotlin (shared AmplitudeAnalyticsHelper).
/// Android core/analytics 와 같은 설정(flushQueueSize 30, flushInterval 30초)을 쓴다.
/// 디버그 빌드에서는 만들지 않는다 — Android 처럼 디버그 이벤트가 운영 프로젝트에 섞이지 않게 하기 위함.
final class AmplitudeAnalyticsBridge {
    private let amplitude: Amplitude

    init(apiKey: String) {
        amplitude = Amplitude(
            configuration: Configuration(
                apiKey: apiKey,
                flushQueueSize: 30,
                flushIntervalMillis: 30000
            )
        )
    }

    func track(eventType: String, properties: [String: Any]?) {
        let eventProperties = (properties?.isEmpty ?? true) ? nil : properties
        amplitude.track(eventType: eventType, eventProperties: eventProperties)
    }

    func setUserId(_ userId: String?) {
        amplitude.setUserId(userId: userId)
    }
}
