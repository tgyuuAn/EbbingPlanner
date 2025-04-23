package com.tgyuu.database.di

import com.tgyuu.database.EbbingDatabase
import com.tgyuu.database.dao.TodoTagsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTodoTagsDao(
        database: EbbingDatabase,
    ): TodoTagsDao = database.todoTagsDao()
}	
