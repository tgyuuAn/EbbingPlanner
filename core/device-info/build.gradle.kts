plugins {
    id("ebbing.android.library")
}

android {
    namespace = "com.tgyuu.deviceinfo"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(libs.androidx.datastore)
    implementation(libs.koin.android)
}
