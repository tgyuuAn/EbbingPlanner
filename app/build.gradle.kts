import java.util.Properties

plugins {
    id("ebbing.android.application")
    id("ebbing.android.compose")
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
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

    signingConfigs {
        val keystoreProperties = Properties()
        keystoreProperties.load(
            project.rootProject.file("keystore.properties").bufferedReader()
        )
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

dependencies {
    implementation(libs.androidx.core.splashscreen)

    implementation(projects.core.designsystem)
    implementation(projects.core.navigation)
    implementation(projects.core.commonUi)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.domain)

    implementation(projects.feature.home)
    implementation(projects.feature.dashboard)
    implementation(projects.feature.setting)
    implementation(projects.feature.alarm)
}
