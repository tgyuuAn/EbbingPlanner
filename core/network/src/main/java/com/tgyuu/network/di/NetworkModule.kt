package com.tgyuu.network.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.tgyuu.network.source.sync.SyncDataSource
import com.tgyuu.network.source.sync.SyncDataSourceImpl
import dagger.Binds
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

    @Singleton
    @Provides
    fun provideFirebaseFireStore(): FirebaseFirestore = Firebase.firestore
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Debug

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Release

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkBindModule {

    @Binds
    @Singleton
    abstract fun bindSyncDataSource(
        syncDataSourceImpl: SyncDataSourceImpl
    ): SyncDataSource
}
