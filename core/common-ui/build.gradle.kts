plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.common.ui"

    buildTypes { release { consumerProguardFiles("consumer-rules.pro") } }
}
