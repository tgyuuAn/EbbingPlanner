package com.tgyuu.shared.di

import com.tgyuu.shared.database.EbbingDatabase
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
}

/**
 * Returns all shared modules
 */
fun getSharedModules(): List<Module> = listOf(sharedModule)
