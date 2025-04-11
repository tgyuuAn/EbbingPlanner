plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidHilt") {
            id = "ebbing.android.hilt"
            implementationClass = "com.tgyuu.build.logic.HiltAndroidPlugin"
        }
        register("kotlinHilt") {
            id = "ebbing.kotlin.hilt"
            implementationClass = "com.tgyuu.build.logic.HiltKotlinPlugin"
        }
    }
}
