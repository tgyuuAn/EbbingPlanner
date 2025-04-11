plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
}

android {
    namespace = "com.tgyuu.common.ui"

    buildTypes { release { consumerProguardFiles("consumer-rules.pro") } }
}
