import java.util.Properties

plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
}

android {
    namespace = "com.tgyuu.analytics"

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").bufferedReader())
        buildConfigField(
            "String",
            "AMPLITUDE_API_KEY",
            "\"${properties["AMPLITUDE_API_KEY"]}\"",
        )
    }

    buildFeatures{
        buildConfig = true
    }
}

dependencies {
    implementation(libs.amplitude.analytics)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.koin.android)
}
