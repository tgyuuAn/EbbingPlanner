plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.common)
}
