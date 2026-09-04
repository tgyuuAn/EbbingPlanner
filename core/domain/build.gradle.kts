plugins {
    id("ebbing.kotlin.library")
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
