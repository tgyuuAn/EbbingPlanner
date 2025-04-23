package com.tgyuu.data.di

import com.tgyuu.data.repository.TodoRepositoryImpl
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
}
