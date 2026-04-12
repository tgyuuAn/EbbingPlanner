package com.tgyuu.shared.di

import com.tgyuu.shared.data.repository.ConfigRepositoryImpl
import com.tgyuu.shared.data.repository.ErrorRepositoryImpl
import com.tgyuu.shared.data.repository.SyncRepositoryImpl
import com.tgyuu.shared.data.repository.TodoRepositoryImpl
import com.tgyuu.shared.data.source.StubSyncDataSource
import com.tgyuu.shared.data.source.SyncDataSource
import com.tgyuu.shared.database.EbbingDatabase
import com.tgyuu.shared.domain.model.ErrorBus
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ErrorRepository
import com.tgyuu.shared.domain.repository.SyncRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.DebugAnalyticsHelper
import com.tgyuu.shared.platform.DebugErrorDataSource
import com.tgyuu.shared.platform.ErrorDataSource
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Shared module for Koin DI - provides DAOs from database
 * Database instance must be provided by platform-specific modules
 */
val sharedModule = module {
    // DAOs - database provided by platform module
    single { get<EbbingDatabase>().todoTagsDao() }
    single { get<EbbingDatabase>().schedulesDao() }
    single { get<EbbingDatabase>().todoWithSchedulesDao() }
    single { get<EbbingDatabase>().repeatCyclesDao() }
    single { get<EbbingDatabase>().syncDao() }

    // Repository
    single<TodoRepository> {
        TodoRepositoryImpl(
            todoTagsDao = get(),
            todoSchedulesDao = get(),
            todoWithSchedulesDao = get(),
            repeatCyclesDao = get(),
        )
    }

    single<ConfigRepository> {
        ConfigRepositoryImpl(settings = get())
    }

    single<ErrorDataSource> { DebugErrorDataSource() }

    single<ErrorRepository> { ErrorRepositoryImpl(errorDataSource = get()) }

    single { ErrorBus(get()) }

    single<SyncDataSource> { StubSyncDataSource() }

    single<SyncRepository> {
        SyncRepositoryImpl(
            settings = get(),
            syncDataSource = get(),
            syncDao = get(),
            schedulesDao = get(),
        )
    }

    single<AnalyticsHelper> { DebugAnalyticsHelper() }
}

/**
 * Returns all shared modules
 */
fun getSharedModules(): List<Module> = listOf(sharedModule)
