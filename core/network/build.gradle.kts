plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tgyuu.network"

    buildTypes { release { consumerProguardFiles("consumer-rules.pro") } }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
}
