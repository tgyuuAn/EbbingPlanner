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
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.profileinstaller)

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
