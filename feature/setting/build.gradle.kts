import java.util.Properties

plugins {
    id("ebbing.android.feature")
}

android {
    namespace = "com.tgyuu.setting"

    defaultConfig {
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").bufferedReader())
        buildConfigField(
            type = "String",
            name = "EBBING_TALK_URL",
            value = "\"${localProperties["EBBING_TALK_URL"]}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.analytics)
    implementation(projects.core.experiment.domain)
    implementation(projects.core.alarm)
    implementation(projects.core.inAppReview)
    implementation(projects.core.inAppUpdate)

    implementation(libs.accompanist.permission)
}
