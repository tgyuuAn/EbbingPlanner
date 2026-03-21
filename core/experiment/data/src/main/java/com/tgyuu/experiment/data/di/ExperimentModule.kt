package com.tgyuu.experiment.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tgyuu.common.initializer.Initializer
import com.tgyuu.experiment.data.datasource.ExperimentLocalDataSource
import com.tgyuu.experiment.data.datasource.ExperimentLocalDataSourceImpl
import com.tgyuu.experiment.data.datasource.ExperimentRemoteDataSource
import com.tgyuu.experiment.data.datasource.ExperimentRemoteDataSourceImpl
import com.tgyuu.experiment.data.initializer.ExperimentInitializer
import com.tgyuu.experiment.data.repository.ExperimentRepositoryImpl
import com.tgyuu.experiment.domain.repository.ExperimentRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

private const val EXPERIMENT_DATASTORE_NAME = "EXPERIMENT_PREFERENCES"
private val Context.experimentDataStore by preferencesDataStore(name = EXPERIMENT_DATASTORE_NAME)

val experimentModule = module {
    single<DataStore<Preferences>>(named("experiment")) { androidContext().experimentDataStore }

    single { ExperimentLocalDataSourceImpl(get(named("experiment"))) } bind ExperimentLocalDataSource::class
    single { ExperimentRemoteDataSourceImpl(get()) } bind ExperimentRemoteDataSource::class
    single { ExperimentRepositoryImpl(get(), get()) } bind ExperimentRepository::class
    single { ExperimentInitializer(get()) } bind Initializer::class
}
