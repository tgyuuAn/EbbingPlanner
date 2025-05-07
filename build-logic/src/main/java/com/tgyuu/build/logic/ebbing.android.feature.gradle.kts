import com.tgyuu.build.logic.configureHiltAndroid
import com.tgyuu.build.logic.libs

plugins {
    id("ebbing.android.library")
    id("ebbing.android.compose")
}

android {
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

configureHiltAndroid()

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))
    implementation(project(":core:navigation"))
    implementation(project(":core:common-ui"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("kotlinx.serialization.json").get())
    implementation(libs.findLibrary("androidx.compose.navigation").get())
    implementation(libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
    implementation(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
    implementation(libs.findLibrary("androidx.adaptive").get())
    androidTestImplementation(libs.findLibrary("androidx.compose.ui.test").get())
    debugImplementation(libs.findLibrary("androidx.compose.ui.test.manifest").get())
}
