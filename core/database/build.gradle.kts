plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
    id("androidx.room")
}

android {
    namespace = "com.tgyuu.database"

    sourceSets { getByName("androidTest").assets.directories += "schemas" }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    androidTestImplementation(libs.androidx.room.testing)
}
