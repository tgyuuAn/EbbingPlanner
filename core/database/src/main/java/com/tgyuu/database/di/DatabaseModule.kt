package com.tgyuu.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tgyuu.database.EbbingDatabase
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
    ).addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // todo_tag 삭제 후 todo_info.tagId 를 0으로!
            db.execSQL(
                """
                  CREATE TRIGGER reset_tag_id_after_delete
                  AFTER DELETE ON todo_tag
                  BEGIN
                    UPDATE todo_info
                    SET tagId = 0
                    WHERE tagId = OLD.id;
                  END;
               """.trimIndent()
            )
        }
    }).build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindsModule {

    @Binds
    @Singleton
    abstract fun bindsLocalProfileDataSource(
        localTagDataSourceImpl: LocalTagDataSourceImpl
    ): LocalTagDataSource

    @Binds
    @Singleton
    abstract fun bindsLocalProfileDataSource(
        localTodoDataSourceImpl: LocalTodoDataSourceImpl
    ): LocalTodoDataSource
}
