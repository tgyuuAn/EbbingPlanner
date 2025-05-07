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
            name = "EBBING_CHANNEL_TALK_URL",
            value = "\"${localProperties["EBBING_CHANNEL_TALK_URL"]}\""
        )
        buildConfigField(
            type = "String",
            name = "EBBING_TERMS_OF_USE_URL",
            value = "\"${localProperties["EBBING_TERMS_OF_USE_URL"]}\""
        )
        buildConfigField(
            type = "String",
            name = "EBBING_PRIVACY_AND_POLICY_URL",
            value = "\"${localProperties["EBBING_PRIVACY_AND_POLICY_URL"]}\""
        )
        buildConfigField(
            type = "String",
            name = "EBBING_NOTICE_URL",
            value = "\"${localProperties["EBBING_NOTICE_URL"]}\""
        )
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.feature.alarm)

    implementation(libs.accompanist.permission)
}
