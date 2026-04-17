package com.tgyuu.shared.di

import com.tgyuu.shared.database.EbbingDatabase
import com.tgyuu.shared.database.createEbbingDatabase
import com.tgyuu.shared.platform.InAppReviewManager
import com.tgyuu.shared.platform.Settings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Android-specific Koin module - provides database instance
 */
val androidModule = module {
    single<EbbingDatabase> { createEbbingDatabase(androidContext()) }
    single { Settings(androidContext()) }
    single { InAppReviewManager(activity = null) }  // Activity-aware review handled by feature layer
}

/**
 * Returns all Android modules including shared
 */
fun getAndroidModules() = listOf(androidModule) + getSharedModules()
