package com.tgyuu.ebbingplanner.di

import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.analytics.di.analyticsModule
import com.tgyuu.common.event.EventBus
import com.tgyuu.common.initializer.Initializer
import com.tgyuu.dashboard.di.scheduleModule
import com.tgyuu.data.di.dataModule
import com.tgyuu.database.di.databaseModule
import com.tgyuu.datastore.di.datastoreModule
import com.tgyuu.di.alarmModule
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.ebbingplanner.MainViewModel
import com.tgyuu.ebbingplanner.alarm.NotificationHelperImpl
import com.tgyuu.experiment.data.di.experimentModule
import com.tgyuu.home.di.homeModule
import com.tgyuu.inappreview.InAppReviewManager
import com.tgyuu.inappupdate.InAppUpdateManager
import com.tgyuu.memo.di.memoModule
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.network.di.networkModule
import com.tgyuu.onboarding.di.onboardingModule
import com.tgyuu.repeatcycle.di.repeatCycleModule
import com.tgyuu.setting.di.settingModule
import com.tgyuu.sync.di.syncModule
import com.tgyuu.sync.network.NetworkMonitor
import com.tgyuu.tag.di.tagModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { NotificationHelperImpl(get()) } bind NotificationHelper::class
    single { NavigationBus() }
    single { EventBus() }
    single { NetworkMonitor(androidContext()) }
    single { ErrorBus(get()) }
    single { InAppReviewManager(androidContext()) }
    single { InAppUpdateManager(androidContext()) }

    // ViewModel
    viewModelOf(::MainViewModel)

    // Collect all Initializers
    single<Set<Initializer>> {
        getAll<Initializer>().toSet()
    }
}

// All Koin modules for the app
val appModules = listOf(
    // Core modules
    appModule,
    databaseModule,
    datastoreModule,
    networkModule,
    dataModule,
    alarmModule,
    analyticsModule,
    experimentModule,
    // Feature modules
    homeModule,
    tagModule,
    settingModule,
    syncModule,
    scheduleModule,
    memoModule,
    repeatCycleModule,
    onboardingModule,
)
