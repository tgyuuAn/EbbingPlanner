package com.tgyuu.build.logic

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal fun Project.configureKotlinAndroid() {
    androidExtension.apply {
        compileSdk = 35

        defaultConfig.minSdk = 28

        buildTypes.getByName("release").apply {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        compileOptions.apply {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        packaging.resources.apply {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }

    val libs = extensions.libs
    dependencies {
        val bom = libs.findLibrary("firebase-bom").get()
        add("implementation", platform(bom))
        add("implementation", libs.findLibrary("firebase-analytics").get())
        add("implementation", libs.findLibrary("firebase-crashlytics").get())
    }

    configureKotlin()
}

internal fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)

            val warningsAsErrors = project.findProperty("warningsAsErrors") as? String
            allWarningsAsErrors.set(warningsAsErrors.toBoolean())
            freeCompilerArgs.set(
                freeCompilerArgs.get() + listOf(
                    "-opt-in=kotlin.RequiresOptIn",
                )
            )
        }
    }
}
