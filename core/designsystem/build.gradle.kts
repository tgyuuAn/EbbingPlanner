plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
}

android {
    namespace = "com.tgyuu.designsystem"
}

dependencies {
    implementation(projects.core.commonUi)
    implementation(projects.core.domain)

    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}
