import java.util.Properties

plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.analytics.data"

    defaultConfig {
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").bufferedReader())
        buildConfigField(
            "String",
            "AMPLITUDE_API_KEY",
            "\"${properties["AMPLITUDE_API_KEY"]}\"",
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(projects.core.analytics.domain)

    implementation(libs.amplitude.analytics)
}
