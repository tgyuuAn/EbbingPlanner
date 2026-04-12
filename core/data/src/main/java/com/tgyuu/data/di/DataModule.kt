package com.tgyuu.data.di

import com.tgyuu.common.initializer.Initializer
import com.tgyuu.data.initializer.MigrationInitializer
import com.tgyuu.data.repository.ConfigRepositoryImpl
import com.tgyuu.data.repository.ErrorRepositoryImpl
import com.tgyuu.data.repository.SyncRepositoryImpl
import com.tgyuu.data.repository.TodoRepositoryImpl
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.ErrorRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindTodoRepository(todoRepositoryImpl: TodoRepositoryImpl): TodoRepository

    @Binds
    @Singleton
    abstract fun bindConfigRepository(configRepositoryImpl: ConfigRepositoryImpl): ConfigRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(syncRepositoryImpl: SyncRepositoryImpl): SyncRepository

    @Binds
    @Singleton
    abstract fun bindErrorRepository(errorRepositoryImpl: ErrorRepositoryImpl): ErrorRepository

    @Binds
    @IntoSet
    abstract fun bindMigrationInitializer(impl: MigrationInitializer): Initializer
}
