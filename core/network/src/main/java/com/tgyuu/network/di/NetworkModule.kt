package com.tgyuu.network.di

import android.content.Context
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.tgyuu.network.BuildConfig
import com.tgyuu.network.source.error.DebugErrorDataSourceImpl
import com.tgyuu.network.source.error.ErrorDataSource
import com.tgyuu.network.source.error.ErrorDataSourceImpl
import com.tgyuu.network.source.log.HeapDumpWorker.Companion.HPROF_DIR
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkProvidesModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

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

    @Singleton
    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics = FirebaseCrashlytics.getInstance()

    @Provides
    @Singleton
    @Debug
    fun provideDebugErrorDataSource(debugErrorDataSourceImpl: DebugErrorDataSourceImpl): ErrorDataSource =
        debugErrorDataSourceImpl

    @Provides
    @Singleton
    @Release
    fun provideReleaseErrorDataSource(errorDataSourceImpl: ErrorDataSourceImpl): ErrorDataSource =
        errorDataSourceImpl

    @Provides
    @Singleton
    fun provideErrorDataSource(
        @Debug debugErrorDataSource: ErrorDataSource,
        @Release releaseErrorDataSource: ErrorDataSource,
    ): ErrorDataSource {
        return if (BuildConfig.DEBUG) debugErrorDataSource
        else releaseErrorDataSource
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = Firebase.storage
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Debug

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Release
