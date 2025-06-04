package com.tgyuu.database.di

import android.content.Context
import androidx.room.Room
import com.tgyuu.database.EbbingDatabase
import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.tag.LocalTagDataSourceImpl
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseProvidesModule {
    @Provides
    @Singleton
    fun providesPieceDatabase(
        @ApplicationContext context: Context,
    ): EbbingDatabase = Room.databaseBuilder(
        context,
        EbbingDatabase::class.java,
        EbbingDatabase.NAME,
    ).build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindsModule {

    @Binds
    @Singleton
    abstract fun bindsLocalTagDataSource(
        localTagDataSourceImpl: LocalTagDataSourceImpl
    ): LocalTagDataSource

    @Binds
    @Singleton
    abstract fun bindsLocalTodoDataSource(
        localTodoDataSourceImpl: LocalTodoDataSourceImpl
    ): LocalTodoDataSource

    @Binds
    @Singleton
    abstract fun bindsLocalRepeatCycleDataSource(
        localRepeatCycleDataSource: LocalRepeatCycleDataSource
    ): LocalRepeatCycleDataSource
}
