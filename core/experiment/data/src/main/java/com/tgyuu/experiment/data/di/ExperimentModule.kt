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
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class Experiment

@Module
@InstallIn(SingletonComponent::class)
object ExperimentProvidesModule {
    private const val EXPERIMENT_DATASTORE_NAME = "EXPERIMENT_PREFERENCES"
    private val Context.experimentDataStore by preferencesDataStore(name = EXPERIMENT_DATASTORE_NAME)

    @Provides
    @Singleton
    @Experiment
    fun provideExperimentDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.experimentDataStore
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ExperimentBindsModule {

    @Binds
    @Singleton
    abstract fun bindExperimentRepository(
        impl: ExperimentRepositoryImpl,
    ): ExperimentRepository

    @Binds
    @Singleton
    abstract fun bindExperimentRemoteDataSource(
        impl: ExperimentRemoteDataSourceImpl,
    ): ExperimentRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindExperimentLocalDataSource(
        impl: ExperimentLocalDataSourceImpl,
    ): ExperimentLocalDataSource

    @Binds
    @IntoSet
    abstract fun bindExperimentInitializer(
        impl: ExperimentInitializer,
    ): Initializer
}
