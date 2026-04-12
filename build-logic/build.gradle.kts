plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)

    // Compose Multiplatform
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.7.0")
}

// Hilt plugins removed - migrating to Koin
