plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    // Suppress expect/actual warning
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    // AGP 9+ KMP 전용 안드로이드 라이브러리 타깃 (구 androidTarget + android {} 블록 대체)
    androidLibrary {
        namespace = "com.tgyuu.shared"
        compileSdk = 35
        minSdk = 28

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // iosX64(Intel 시뮬레이터)는 CMP 1.11부터 미지원이라 제거
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.collections.immutable)

            // Compose Multiplatform
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(libs.compose.material.icons.core)

            // Decompose Navigation
            implementation(libs.decompose)
            implementation(libs.decompose.compose)

            // KMP ViewModel
            implementation(libs.lifecycle.viewmodel.kmp)
            implementation(libs.lifecycle.viewmodel.compose.kmp)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel.kmp)

            // Room KMP
            implementation(libs.androidx.room.runtime)

            // Ktor HTTP Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)

            // Supabase (KMP) - sync backend
            implementation(libs.supabase.postgrest)

            // QR code rendering (Compose Multiplatform)
            implementation(libs.qrose)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.coroutines.test)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.play.review.ktx)
        }

        iosMain.dependencies {
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.ktor.client.darwin)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}
