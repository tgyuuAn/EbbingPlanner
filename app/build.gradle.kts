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
        versionCode = 36
        versionName = "1.0.35"
        targetSdk = 35

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").bufferedReader())

        buildConfigField(
            type = "String",
            name = "EBBING_NOTICE_URL",
            value = "\"${localProperties["EBBING_NOTICE_URL"]}\""
        )
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
    implementation(projects.core.alarm)
    implementation(projects.core.analytics)
    implementation(projects.core.inAppReview)
    implementation(projects.core.experiment.data)

    implementation(projects.feature.onboarding)
    implementation(projects.feature.home)
    implementation(projects.feature.memo)
    implementation(projects.feature.schedule)
    implementation(projects.feature.setting)
    implementation(projects.feature.tag)
    implementation(projects.feature.repeatcycle)
    implementation(projects.feature.sync)
    implementation(projects.feature.widget)
    baselineProfile(projects.baselineprofile)

    testImplementation(libs.mockk)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.adaptive.navigation.suite)
}
