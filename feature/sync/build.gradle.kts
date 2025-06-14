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
}
