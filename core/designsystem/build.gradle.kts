plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
}

android {
    namespace = "com.tgyuu.designsystem"
}

dependencies {
    implementation(projects.core.commonUi)

    implementation(libs.coil.compose)
    implementation(libs.coil.network)
}
