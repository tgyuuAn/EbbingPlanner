plugins {
    id("ebbing.android.library")
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
    implementation(libs.koin.android)
}
