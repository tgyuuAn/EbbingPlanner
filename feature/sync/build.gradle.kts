import java.util.Properties

plugins {
    id("ebbing.android.feature")
}

android {
    namespace = "com.tgyuu.sync"

    defaultConfig {
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").bufferedReader())
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.analytics)

    implementation(libs.kotlinx.datetime)
    implementation(libs.mlkit.barcode.scanning)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.qrose)
    implementation(libs.accompanist.permission)

    testImplementation(libs.mockk)
}
