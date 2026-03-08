plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.inappreview"
}

dependencies {
    implementation(libs.play.review.ktx)
}
