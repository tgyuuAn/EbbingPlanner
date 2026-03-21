import SwiftUI
import shared

struct TagListView: View {
    @StateObject private var viewModel = TagListViewModel()
    @State private var showAddTagSheet = false

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.tags.isEmpty {
                    VStack {
                        Spacer()
                        Text("태그가 없습니다")
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                } else {
                    List {
                        ForEach(viewModel.tags, id: \.id) { tag in
                            TagRowView(tag: tag)
                        }
                        .onDelete { indexSet in
                            viewModel.deleteTag(at: indexSet)
                        }
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("태그")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: { showAddTagSheet = true }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showAddTagSheet) {
                AddTagView { name, color in
                    viewModel.addTag(name: name, color: color)
                    showAddTagSheet = false
                }
            }
        }
        .task {
            await viewModel.loadTags()
        }
    }
}

struct TagRowView: View {
    let tag: TodoTagWrapper

    var body: some View {
        HStack(spacing: 12) {
            Circle()
                .fill(Color(hex: tag.color))
                .frame(width: 24, height: 24)

            Text(tag.name)
                .font(.body)

            Spacer()
        }
        .padding(.vertical, 8)
    }
}

struct AddTagView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var tagName = ""
    @State private var selectedColor: Int32 = 0xFFBBE1FA

    let onAdd: (String, Int32) -> Void

    private let colors: [Int32] = [
        0xFFBBE1FA, // Blue
        0xFFFFB6C1, // Pink
        0xFF98FB98, // Green
        0xFFFFD700, // Gold
        0xFFDDA0DD, // Plum
        0xFFFFA07A, // Salmon
        0xFF87CEEB, // Sky Blue
        0xFFE6E6FA, // Lavender
    ]

    var body: some View {
        NavigationStack {
            Form {
                Section("태그 이름") {
                    TextField("태그 이름 입력", text: $tagName)
                }

                Section("색상") {
                    LazyVGrid(columns: Array(repeating: GridItem(.flexible()), count: 4), spacing: 16) {
                        ForEach(colors, id: \.self) { color in
                            Circle()
                                .fill(Color(hex: color))
                                .frame(width: 44, height: 44)
                                .overlay(
                                    Circle()
                                        .stroke(Color.primary, lineWidth: selectedColor == color ? 3 : 0)
                                )
                                .onTapGesture {
                                    selectedColor = color
                                }
                        }
                    }
                    .padding(.vertical, 8)
                }
            }
            .navigationTitle("태그 추가")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("취소") {
                        dismiss()
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("추가") {
                        onAdd(tagName, selectedColor)
                    }
                    .disabled(tagName.isEmpty)
                }
            }
        }
    }
}

#Preview {
    TagListView()
}
