package com.tgyuu.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tgyuu.database.EbbingDatabase
import com.tgyuu.database.model.tag.TodoTagEntity
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.tag.LocalTagDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseProvidesModule {
    @Provides
    @Singleton
    fun providesEbbingDatabase(
        @ApplicationContext context: Context,
    ): EbbingDatabase {
        lateinit var db: EbbingDatabase

        db = Room.databaseBuilder(
            context,
            EbbingDatabase::class.java,
            EbbingDatabase.NAME
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(dbInterface: SupportSQLiteDatabase) {
                super.onCreate(dbInterface)
                CoroutineScope(Dispatchers.IO).launch {
                    db.todoTagsDao().insertTags(
                        TodoTagEntity(
                            id = 0,
                            name = "미지정",
                            color = 0XFFBBE1FA.toInt(),
                            startDate = LocalDate.now(),
                            createdAt = LocalDate.now(),
                            updatedAt = LocalDate.now()
                        )
                    )
                }
            }
        }).build()

        return db
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindsModule {

    @Binds
    @Singleton
    abstract fun bindsLocalProfileDataSource(
        localTagDataSourceImpl: LocalTagDataSourceImpl
    ): LocalTagDataSource
}
