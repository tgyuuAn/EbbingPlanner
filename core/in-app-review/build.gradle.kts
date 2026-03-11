plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.inappreview"
}

dependencies {
    implementation(projects.core.commonUi)
    implementation(libs.play.review.ktx)
    implementation(libs.coroutines.play.services)
}
