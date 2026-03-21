package com.tgyuu.shared.di

import com.tgyuu.shared.database.EbbingDatabase
import com.tgyuu.shared.database.createEbbingDatabase
import com.tgyuu.shared.database.dao.RepeatCyclesDao
import com.tgyuu.shared.database.dao.TodoSchedulesDao
import com.tgyuu.shared.database.dao.TodoTagsDao
import com.tgyuu.shared.database.dao.TodoWithSchedulesDao
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * iOS-specific Koin module - provides database instance
 */
val iosModule = module {
    single<EbbingDatabase> { createEbbingDatabase() }
}

/**
 * Returns all iOS modules including shared
 */
fun getIosModules() = listOf(iosModule) + getSharedModules()

/**
 * Initialize Koin for iOS
 * Call this from Swift: IosModuleKt.initKoin()
 */
fun initKoin() {
    startKoin {
        modules(getIosModules())
    }
}

/**
 * Koin Helper for iOS - provides access to DAOs from Swift
 * Usage in Swift:
 *   let helper = KoinHelper()
 *   let schedules = try await helper.schedulesDao.loadAllTodoSchedules()
 */
class KoinHelper : KoinComponent {
    val schedulesDao: TodoSchedulesDao by inject()
    val tagsDao: TodoTagsDao by inject()
    val repeatCyclesDao: RepeatCyclesDao by inject()
    val todoWithSchedulesDao: TodoWithSchedulesDao by inject()
}
