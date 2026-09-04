plugins {
    id("ebbing.android.library")
}

android {
    namespace = "com.tgyuu.data"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.common)
    implementation(projects.core.deviceInfo)

    implementation(libs.kotlinx.datetime)
    implementation(libs.koin.core)

    testImplementation(libs.mockk)
}
