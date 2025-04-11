plugins {
    id("ebbing.kotlin.library")
    id("ebbing.kotlin.hilt")
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(projects.core.common)
}
