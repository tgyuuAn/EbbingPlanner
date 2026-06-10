import java.util.Properties

plugins {
    id("ebbing.android.library")
    id("ebbing.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tgyuu.network"

    defaultConfig {
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").bufferedReader())

        buildConfigField("String", "SUPABASE_URL", "\"${localProperties["SUPABASE_URL"]}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties["SUPABASE_ANON_KEY"]}\"")
    }

    buildTypes { release { consumerProguardFiles("consumer-rules.pro") } }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.firebase.config)
    implementation(libs.firebase.storage)
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.android)
}
