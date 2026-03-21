package com.tgyuu.network.di

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.tgyuu.network.BuildConfig
import com.tgyuu.network.source.ConfigDataSource
import com.tgyuu.network.source.SyncDataSource
import com.tgyuu.network.source.error.DebugErrorDataSourceImpl
import com.tgyuu.network.source.error.ErrorDataSource
import com.tgyuu.network.source.error.ErrorDataSourceImpl
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

    single<FirebaseFirestore> { Firebase.firestore }

    single<FirebaseCrashlytics> { FirebaseCrashlytics.getInstance() }

    single { SyncDataSource(get()) }
    single { ConfigDataSource(get(), get()) }

    single<ErrorDataSource>(named("debug")) { DebugErrorDataSourceImpl() }
    single<ErrorDataSource>(named("release")) { ErrorDataSourceImpl(get()) }

    single<ErrorDataSource> {
        if (BuildConfig.DEBUG) get(named("debug")) else get(named("release"))
    }
}
