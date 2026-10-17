plugins {
    id("ebbing.android.library")
}

android {
    namespace = "com.tgyuu.inappupdate"
}

dependencies {
    implementation(projects.core.commonUi)
    implementation(libs.play.app.update.ktx)
    implementation(libs.coroutines.play.services)
}
