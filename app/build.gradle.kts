import java.util.Properties

plugins {
    id("ebbing.android.application")
    id("ebbing.android.compose")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.tgyuu.ebbingplanner"

    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").bufferedReader())
    }

    buildTypes {
        debug { manifestPlaceholders["appName"] = "@string/app_name_dev" }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["appName"] = "@string/app_name"
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.core.splashscreen)

    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)
    implementation(projects.core.navigation)
    implementation(projects.core.commonUi)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(projects.feature.home)
    implementation(projects.feature.dashboard)
    implementation(projects.feature.setting)
}
