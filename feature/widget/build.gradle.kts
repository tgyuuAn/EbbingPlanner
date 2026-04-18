plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
}

android {
    namespace = "com.tgyuu.widget"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.domain)
    implementation(projects.core.analytics)

    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.androidx.glance.preview)
    implementation(libs.gson)
}
