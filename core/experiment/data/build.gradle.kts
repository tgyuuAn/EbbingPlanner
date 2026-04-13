plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.experiment.data"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.experiment.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)

    implementation(libs.firebase.config)
    implementation(libs.androidx.datastore)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.coroutines.test)
}
