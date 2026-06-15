plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.deviceinfo"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(libs.androidx.datastore)
}
