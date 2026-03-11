plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.inappupdate"
}

dependencies {
    implementation(projects.core.commonUi)
    implementation(libs.play.app.update.ktx)
}
