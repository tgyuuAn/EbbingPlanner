import java.util.Properties

plugins {
    id("ebbing.android.application")
    id("ebbing.android.compose")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.android.application)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.tgyuu.ebbingplanner"

    defaultConfig {
        versionCode = 12
        versionName = "1.0.11"
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").bufferedReader())
    }

    signingConfigs {
        val keystoreProperties = Properties()
        keystoreProperties.load(project.rootProject.file("keystore.properties").bufferedReader())
        create("release") {
            storeFile = file(keystoreProperties["STORE_FILE_PATH"] as String)
            storePassword = keystoreProperties["STORE_PASSWORD"] as String
            keyAlias = keystoreProperties["KEY_ALIAS_RELEASE"] as String
            keyPassword = keystoreProperties["KEY_PASSWORD_RELEASE"] as String
        }
    }

    buildTypes {
        debug { manifestPlaceholders["appName"] = "@string/app_name_dev" }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["appName"] = "@string/app_name"
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

baselineProfile {
    dexLayoutOptimization = true
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)
    implementation(projects.core.commonUi)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(projects.feature.onboarding)
    implementation(projects.feature.home)
    implementation(projects.feature.memo)
    implementation(projects.feature.dashboard)
    implementation(projects.feature.setting)
    implementation(projects.feature.alarm)
    implementation(projects.feature.tag)
    implementation(projects.feature.repeatcycle)
    implementation(projects.feature.sync)
    baselineProfile(projects.baselineprofile)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.adaptive.navigation.suite)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.gson)
}
