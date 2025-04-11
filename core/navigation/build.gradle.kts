plugins {
    id("ebbing.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tgyuu.navigation"

    buildTypes { release { consumerProguardFiles("consumer-rules.pro") } }
}

dependencies {
    implementation(libs.androidx.navigation.ui)
    implementation(libs.kotlinx.serialization.json)
}
