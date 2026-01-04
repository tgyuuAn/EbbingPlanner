plugins {
    id("ebbing.kotlin.library")
    id("ebbing.kotlin.hilt")
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
