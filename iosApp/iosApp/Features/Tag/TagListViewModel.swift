import Foundation
import shared

struct TodoTagWrapper: Identifiable {
    let id: Int32
    let name: String
    let color: Int32

    init(from entity: TodoTagEntity) {
        self.id = entity.id
        self.name = entity.name
        self.color = entity.color
    }
}

@MainActor
class TagListViewModel: ObservableObject {
    @Published var tags: [TodoTagWrapper] = []

    private let koinHelper = KoinHelper()

    func loadTags() async {
        do {
            let tagEntities = try await koinHelper.tagsDao.getTags()
            tags = tagEntities.map { TodoTagWrapper(from: $0) }
        } catch {
            print("Failed to load tags: \(error)")
        }
    }

    func addTag(name: String, color: Int32) {
        Task {
            do {
                let now = currentKotlinxLocalDateTime()
                let today = currentKotlinxLocalDate()
                let entity = TodoTagEntity(
                    id: 0,
                    name: name,
                    color: color,
                    createdAt: today,
                    isDeleted: false,
                    updatedAt: now
                )
                _ = try await koinHelper.tagsDao.insertTag(tag: entity)
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
                    let now = currentKotlinxLocalDateTime()
                    try await koinHelper.tagsDao.softDeleteTag(tagId: tag.id, updatedAt: now)
                } catch {
                    print("Failed to delete tag: \(error)")
                }
            }
            await loadTags()
        }
    }
}
