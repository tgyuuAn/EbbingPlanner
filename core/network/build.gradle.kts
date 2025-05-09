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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.firebase.config)
}
