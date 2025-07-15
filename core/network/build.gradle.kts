import java.util.Properties

plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tgyuu.network"

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").bufferedReader())

    buildTypes { release { consumerProguardFiles("consumer-rules.pro") } }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation("androidx.work:work-runtime-ktx:2.10.2")
    implementation(libs.hilt.common)
    implementation(libs.hilt.work)
    implementation(libs.firebase.config)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
}
