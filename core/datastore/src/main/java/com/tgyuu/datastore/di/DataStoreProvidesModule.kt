package com.tgyuu.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tgyuu.datastore.datasource.LocalUserConfigDataSource
import com.tgyuu.datastore.datasource.LocalUserConfigDataSourceImpl
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

    @Provides
    @Singleton
    @Named("config")
    fun provideConfigDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.configDataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreBindsModule {

    @Binds
    @Singleton
    abstract fun bindsLocalUserConfigDataSource(
        localUserConfigDataSourceImpl: LocalUserConfigDataSourceImpl,
    ): LocalUserConfigDataSource
}
