package com.tgyuu.database.di

import com.tgyuu.database.EbbingDatabase
import com.tgyuu.database.dao.LinkedDevicesDao
import com.tgyuu.database.dao.RepeatCyclesDao
import com.tgyuu.database.dao.TodoSchedulesDao
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
    fun providesTodoSchedulesDao(
        database: EbbingDatabase,
    ): TodoSchedulesDao = database.schedulesDao()

    @Provides
    fun providesTodoWithSchedulesDao(
        database: EbbingDatabase,
    ): TodoWithSchedulesDao = database.todoWithSchedulesDao()

    @Provides
    fun providesRepeatCyclesDao(
        database: EbbingDatabase,
    ): RepeatCyclesDao = database.repeatCyclesDao()

    @Provides
    fun providesLinkedDevicesDao(
        database: EbbingDatabase,
    ): LinkedDevicesDao = database.linkedDevicesDao()
}	
