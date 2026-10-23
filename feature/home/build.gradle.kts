import java.util.Properties

plugins {
    id("ebbing.android.feature")
}

android {
    namespace = "com.tgyuu.home"

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
    implementation(projects.core.alarm)
    implementation(projects.core.analytics)
    implementation(projects.core.experiment.domain)
    implementation(projects.core.inAppReview)

    implementation(libs.accompanist.permission)
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.mockk)
}
