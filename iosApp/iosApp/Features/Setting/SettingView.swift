import SwiftUI

struct SettingView: View {
    @AppStorage("notificationEnabled") private var notificationEnabled = true
    @AppStorage("darkModeEnabled") private var darkModeEnabled = false

    var body: some View {
        NavigationStack {
            List {
                Section("알림") {
                    Toggle("알림 활성화", isOn: $notificationEnabled)
                }

                Section("테마") {
                    Toggle("다크 모드", isOn: $darkModeEnabled)
                }

                Section("정보") {
                    HStack {
                        Text("앱 버전")
                        Spacer()
                        Text("1.0.0")
                            .foregroundColor(.secondary)
                    }

                    NavigationLink("개인정보 처리방침") {
                        PrivacyPolicyView()
                    }

                    NavigationLink("이용약관") {
                        TermsOfServiceView()
                    }
                }

                Section("데이터") {
                    Button("데이터 초기화", role: .destructive) {
                        // TODO: Implement data reset
                    }
                }
            }
            .navigationTitle("설정")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct PrivacyPolicyView: View {
    var body: some View {
        ScrollView {
            Text("개인정보 처리방침 내용...")
                .padding()
        }
        .navigationTitle("개인정보 처리방침")
        .navigationBarTitleDisplayMode(.inline)
    }
}

struct TermsOfServiceView: View {
    var body: some View {
        ScrollView {
            Text("이용약관 내용...")
                .padding()
        }
        .navigationTitle("이용약관")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    SettingView()
}
