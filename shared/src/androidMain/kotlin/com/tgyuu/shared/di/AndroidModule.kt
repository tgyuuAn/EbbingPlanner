package com.tgyuu.shared.di

import com.tgyuu.shared.data.source.StubSyncDataSource
import com.tgyuu.shared.data.source.SyncDataSource
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
    // shared 모듈은 Android 앱에서 직접 쓰지 않지만, DI 일관성을 위해 Stub 제공
    single<SyncDataSource> { StubSyncDataSource() }
}

/**
 * Returns all Android modules including shared
 */
fun getAndroidModules() = listOf(androidModule) + getSharedModules()
