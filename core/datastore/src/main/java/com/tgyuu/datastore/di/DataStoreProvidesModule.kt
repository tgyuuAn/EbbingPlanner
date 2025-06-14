package com.tgyuu.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSourceImpl
import com.tgyuu.datastore.datasource.user.LocalUserConfigDataSource
import com.tgyuu.datastore.datasource.user.LocalUserConfigDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreProvidesModule {
    private const val CONFIG_DATASTORE_NAME = "CONFIGS_PREFERENCES"
    private val Context.configDataStore by preferencesDataStore(name = CONFIG_DATASTORE_NAME)

    private const val SYNC_DATASTORE_NAME = "SYNC_PREFERENCES"
    private val Context.syncDataStore by preferencesDataStore(name = SYNC_DATASTORE_NAME)

    @Provides
    @Singleton
    @Named("config")
    fun provideConfigDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.configDataStore

    @Provides
    @Singleton
    @Named("sync")
    fun provideSyncDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.syncDataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreBindsModule {

    @Binds
    @Singleton
    abstract fun bindsLocalUserConfigDataSource(
        localUserConfigDataSourceImpl: LocalUserConfigDataSourceImpl,
    ): LocalUserConfigDataSource

    @Binds
    @Singleton
    abstract fun bindsLocalSyncDataSource(
        localSyncDataSourceImpl: LocalSyncDataSourceImpl
    ): LocalSyncDataSource
}
