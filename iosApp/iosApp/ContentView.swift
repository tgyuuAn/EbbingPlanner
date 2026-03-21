import SwiftUI

struct ContentView: View {
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .tabItem {
                    Image(systemName: "house.fill")
                    Text("홈")
                }
                .tag(0)

            TagListView()
                .tabItem {
                    Image(systemName: "tag.fill")
                    Text("태그")
                }
                .tag(1)

            SettingView()
                .tabItem {
                    Image(systemName: "gearshape.fill")
                    Text("설정")
                }
                .tag(2)
        }
        .tint(.black)
    }
}

#Preview {
    ContentView()
}
