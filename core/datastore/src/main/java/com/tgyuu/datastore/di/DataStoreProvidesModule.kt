package com.tgyuu.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSource
import com.tgyuu.datastore.datasource.sync.LocalSyncDataSourceImpl
import com.tgyuu.datastore.datasource.user.LocalUserConfigDataSource
import com.tgyuu.datastore.datasource.user.LocalUserConfigDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val CONFIG_DATASTORE_NAME = "CONFIGS_PREFERENCES"
private val Context.configDataStore by preferencesDataStore(name = CONFIG_DATASTORE_NAME)

private const val SYNC_DATASTORE_NAME = "SYNC_PREFERENCES"
private val Context.syncDataStore by preferencesDataStore(name = SYNC_DATASTORE_NAME)

val datastoreModule = module {
    single<DataStore<Preferences>>(named("config")) { androidContext().configDataStore }
    single<DataStore<Preferences>>(named("sync")) { androidContext().syncDataStore }

    single { LocalUserConfigDataSourceImpl(get(named("config"))) } bind LocalUserConfigDataSource::class
    single { LocalSyncDataSourceImpl(get(named("sync"))) } bind LocalSyncDataSource::class
}
