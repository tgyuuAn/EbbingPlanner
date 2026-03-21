package com.tgyuu.shared.di

import com.tgyuu.shared.database.EbbingDatabase
import com.tgyuu.shared.database.createEbbingDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module - provides database instance
 */
val androidModule = module {
    single<EbbingDatabase> { createEbbingDatabase(androidContext()) }
}

/**
 * Returns all Android modules including shared
 */
fun getAndroidModules() = listOf(androidModule) + getSharedModules()
