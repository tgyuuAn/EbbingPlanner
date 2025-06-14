package com.tgyuu.data.di

import com.tgyuu.data.repository.ConfigRepositoryImpl
import com.tgyuu.data.repository.SyncRepositoryImpl
import com.tgyuu.data.repository.TodoRepositoryImpl
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindsTodoRepository(
        todoRepositoryImpl: TodoRepositoryImpl,
    ): TodoRepository

    @Binds
    @Singleton
    abstract fun bindsConfigRepository(
        configRepositoryImpl: ConfigRepositoryImpl
    ): ConfigRepository

    @Binds
    @Singleton
    abstract fun bindsSyncRepository(
        syncRepositoryImpl: SyncRepositoryImpl
    ): SyncRepository
}
