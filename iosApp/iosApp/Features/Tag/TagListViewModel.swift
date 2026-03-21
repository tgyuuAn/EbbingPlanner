import Foundation
import shared

struct TodoTagWrapper: Identifiable {
    let id: Int32
    let name: String
    let color: Int32

    init(from tag: TodoTag) {
        self.id = tag.id
        self.name = tag.name
        self.color = tag.color
    }
}

@MainActor
class TagListViewModel: ObservableObject {
    @Published var tags: [TodoTagWrapper] = []

    private let koinHelper = KoinHelper()

    func loadTags() async {
        do {
            let tagEntities = try await koinHelper.tagsDao.loadAllTags()
            tags = tagEntities.map { TodoTagWrapper(from: $0) }
        } catch {
            print("Failed to load tags: \(error)")
        }
    }

    func addTag(name: String, color: Int32) {
        Task {
            do {
                _ = try await koinHelper.tagsDao.insertTag(
                    tag: TodoTagEntity(
                        id: 0, // Auto-generated
                        name: name,
                        color: color,
                        createdAt: DateUtilKt.now(Kotlinx_datetimeLocalDate.companion),
                        updatedAt: DateUtilKt.now(Kotlinx_datetimeLocalDateTime.companion),
                        isDeleted: false
                    )
                )
                await loadTags()
            } catch {
                print("Failed to add tag: \(error)")
            }
        }
    }

    func deleteTag(at indexSet: IndexSet) {
        Task {
            for index in indexSet {
                let tag = tags[index]
                do {
                    try await koinHelper.tagsDao.softDeleteTag(
                        id: tag.id,
                        updatedAt: DateUtilKt.now(Kotlinx_datetimeLocalDateTime.companion)
                    )
                } catch {
                    print("Failed to delete tag: \(error)")
                }
            }
            await loadTags()
        }
    }
}
