plugins {
    id("ebbing.android.feature")
}

android {
    namespace = "com.tgyuu.onboarding"
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.analytics)
}
