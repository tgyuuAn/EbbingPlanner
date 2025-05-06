plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
}

android {
    namespace = "com.tgyuu.datastore"

    buildTypes {
        release { consumerProguardFiles("consumer-rules.pro") }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.androidx.datastore)
}
