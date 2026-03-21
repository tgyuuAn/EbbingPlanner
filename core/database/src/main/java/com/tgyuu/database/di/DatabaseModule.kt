package com.tgyuu.database.di

import androidx.room.Room
import com.tgyuu.database.DatabaseMigrations
import com.tgyuu.database.EbbingDatabase
import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSource
import com.tgyuu.database.source.repeatcycle.LocalRepeatCycleDataSourceImpl
import com.tgyuu.database.source.sync.LocalSyncTransactionDataSource
import com.tgyuu.database.source.sync.LocalSyncTransactionDataSourceImpl
import com.tgyuu.database.source.tag.LocalTagDataSource
import com.tgyuu.database.source.tag.LocalTagDataSourceImpl
import com.tgyuu.database.source.todo.LocalTodoDataSource
import com.tgyuu.database.source.todo.LocalTodoDataSourceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            EbbingDatabase::class.java,
            EbbingDatabase.NAME,
        )
            .addMigrations(DatabaseMigrations.MIGRATION_1_TO_2)
            .addMigrations(DatabaseMigrations.MIGRATION_2_TO_3)
            .addMigrations(DatabaseMigrations.MIGRATION_3_TO_4)
            .build()
    }

    // DAOs
    single { get<EbbingDatabase>().todoTagsDao() }
    single { get<EbbingDatabase>().schedulesDao() }
    single { get<EbbingDatabase>().todoWithSchedulesDao() }
    single { get<EbbingDatabase>().repeatCyclesDao() }
    single { get<EbbingDatabase>().syncDao() }

    // DataSources
    single { LocalTagDataSourceImpl(get()) } bind LocalTagDataSource::class
    single { LocalTodoDataSourceImpl(get(), get()) } bind LocalTodoDataSource::class
    single { LocalRepeatCycleDataSourceImpl(get()) } bind LocalRepeatCycleDataSource::class
    single { LocalSyncTransactionDataSourceImpl(get()) } bind LocalSyncTransactionDataSource::class
}
