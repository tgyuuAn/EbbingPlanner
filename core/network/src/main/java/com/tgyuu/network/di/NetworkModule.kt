package com.tgyuu.network.di

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.tgyuu.network.BuildConfig
import com.tgyuu.network.source.ConfigDataSource
import com.tgyuu.network.source.FeatureFlagDataSource
import com.tgyuu.network.source.NotificationLogDataSource
import com.tgyuu.network.source.SupabaseSyncDataSource
import com.tgyuu.network.source.SyncRemoteDataSource
import com.tgyuu.network.source.error.DebugErrorDataSourceImpl
import com.tgyuu.network.source.error.ErrorDataSource
import com.tgyuu.network.source.error.ErrorDataSourceImpl
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    single<Json> {
        Json { ignoreUnknownKeys = true }
    }

    single<FirebaseRemoteConfig> {
        Firebase.remoteConfig.apply {
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
            setConfigSettingsAsync(configSettings)
        }
    }

    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        ) {
            install(Postgrest)
        }
    }

    single<FirebaseCrashlytics> { FirebaseCrashlytics.getInstance() }

    single { SupabaseSyncDataSource(get()) } bind SyncRemoteDataSource::class
    single { ConfigDataSource(get(), get()) }
    single { FeatureFlagDataSource(get()) }
    single { NotificationLogDataSource(get()) }

    single<ErrorDataSource>(named("debug")) { DebugErrorDataSourceImpl() }
    single<ErrorDataSource>(named("release")) { ErrorDataSourceImpl(get()) }

    single<ErrorDataSource> {
        if (BuildConfig.DEBUG) get(named("debug")) else get(named("release"))
    }
}
