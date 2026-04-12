import Foundation
import FirebaseFirestore

/// Bridge class that provides Firebase Firestore operations to Kotlin via KoinHelper.
/// The shared module's SyncDataSource uses Ktor REST API as a cross-platform solution,
/// but this native bridge can be used for better performance and offline support.
class FirebaseSyncBridge {
    static let shared = FirebaseSyncBridge()
    private let db = Firestore.firestore()

    func uploadSchedule(uuid: String, scheduleId: Int, data: [String: Any]) async throws {
        try await db.collection("users").document(uuid)
            .collection("schedules").document(String(scheduleId))
            .setData(data, merge: true)
    }

    func uploadTodoInfo(uuid: String, infoId: Int, data: [String: Any]) async throws {
        try await db.collection("users").document(uuid)
            .collection("todoInfos").document(String(infoId))
            .setData(data, merge: true)
    }

    func uploadTag(uuid: String, tagId: Int, data: [String: Any]) async throws {
        try await db.collection("users").document(uuid)
            .collection("tags").document(String(tagId))
            .setData(data, merge: true)
    }

    func uploadRepeatCycle(uuid: String, cycleId: Int, data: [String: Any]) async throws {
        try await db.collection("users").document(uuid)
            .collection("repeatCycles").document(String(cycleId))
            .setData(data, merge: true)
    }

    func updateSyncTimestamp(uuid: String) async throws {
        try await db.collection("users").document(uuid)
            .collection("info").document("0")
            .setData(["lastUpdatedAt": FieldValue.serverTimestamp()], merge: true)
    }

    func getLastSyncTime(uuid: String) async -> Date? {
        do {
            let doc = try await db.collection("users").document(uuid)
                .collection("info").document("0")
                .getDocument()
            return (doc.data()?["lastUpdatedAt"] as? Timestamp)?.dateValue()
        } catch {
            return nil
        }
    }

    func generateConnectCode(uuid: String, code: String) async throws -> Date {
        let expiration = Date().addingTimeInterval(600) // 10 minutes
        try await db.collection("connectCodes").document(code).setData([
            "uuid": uuid,
            "connectCode": code,
            "connectCodeExpirationTime": Timestamp(date: expiration),
        ])
        return expiration
    }

    func connectAnother(code: String) async throws -> (uuid: String, code: String, expiration: Date)? {
        let doc = try await db.collection("connectCodes").document(code).getDocument()
        guard let data = doc.data(),
              let uuid = data["uuid"] as? String,
              let connectCode = data["connectCode"] as? String,
              let expTs = data["connectCodeExpirationTime"] as? Timestamp else {
            return nil
        }
        let expiration = expTs.dateValue()
        guard expiration > Date() else { return nil }
        return (uuid: uuid, code: connectCode, expiration: expiration)
    }

    func downloadSchedules(uuid: String, since: Date) async throws -> [[String: Any]] {
        let snapshot = try await db.collection("users").document(uuid)
            .collection("schedules")
            .whereField("uploadedAt", isGreaterThan: Timestamp(date: since))
            .getDocuments()
        return snapshot.documents.map { $0.data() }
    }

    func downloadTodoInfos(uuid: String, since: Date) async throws -> [[String: Any]] {
        let snapshot = try await db.collection("users").document(uuid)
            .collection("todoInfos")
            .whereField("uploadedAt", isGreaterThan: Timestamp(date: since))
            .getDocuments()
        return snapshot.documents.map { $0.data() }
    }

    func downloadTags(uuid: String, since: Date) async throws -> [[String: Any]] {
        let snapshot = try await db.collection("users").document(uuid)
            .collection("tags")
            .whereField("uploadedAt", isGreaterThan: Timestamp(date: since))
            .getDocuments()
        return snapshot.documents.map { $0.data() }
    }

    func downloadRepeatCycles(uuid: String, since: Date) async throws -> [[String: Any]] {
        let snapshot = try await db.collection("users").document(uuid)
            .collection("repeatCycles")
            .whereField("uploadedAt", isGreaterThan: Timestamp(date: since))
            .getDocuments()
        return snapshot.documents.map { $0.data() }
    }

    func disconnectDevice(uuid: String) async throws {
        // Remove linked UUID from local settings handled by Kotlin side
    }
}
