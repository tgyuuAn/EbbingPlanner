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
    implementation(projects.core.common)

    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.metrics.performance)
}
