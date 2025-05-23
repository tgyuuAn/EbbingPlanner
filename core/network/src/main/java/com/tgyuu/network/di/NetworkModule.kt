package com.tgyuu.network.di

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkProvidesModule {

    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Singleton
    @Provides
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig.apply {
        val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
        setConfigSettingsAsync(configSettings)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Debug

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Release
