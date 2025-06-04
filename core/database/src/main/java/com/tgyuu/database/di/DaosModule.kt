package com.tgyuu.database.di

import com.tgyuu.database.EbbingDatabase
import com.tgyuu.database.dao.RepeatCyclesDao
import com.tgyuu.database.dao.SchedulesDao
import com.tgyuu.database.dao.TodoTagsDao
import com.tgyuu.database.dao.TodoWithSchedulesDao
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

    @Provides
    fun providesSchedulesDao(
        database: EbbingDatabase,
    ): SchedulesDao = database.schedulesDao()

    @Provides
    fun providesTodoWithSchedulesDao(
        database: EbbingDatabase,
    ): TodoWithSchedulesDao = database.todoWithSchedulesDao()

    @Provides
    fun providesRepeatCycleDao(
        database: EbbingDatabase,
    ): RepeatCyclesDao = database.repeatCyclesDao()
}	
