plugins {
    id("ebbing.android.library")
}

android {
    namespace = "com.tgyuu.inappreview"
}

dependencies {
    implementation(projects.core.commonUi)
    implementation(libs.play.review.ktx)
    implementation(libs.coroutines.play.services)
}
