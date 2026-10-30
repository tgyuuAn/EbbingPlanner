package com.tgyuu.shared.di

import com.tgyuu.shared.data.repository.ConfigRepositoryImpl
import com.tgyuu.shared.data.repository.ErrorRepositoryImpl
import com.tgyuu.shared.data.repository.ExperimentRepositoryImpl
import com.tgyuu.shared.data.repository.FeatureFlagRepositoryImpl
import com.tgyuu.shared.data.repository.SyncRepositoryImpl
import com.tgyuu.shared.data.repository.TodoRepositoryImpl
import com.tgyuu.shared.data.source.UsageOrderStore
import com.tgyuu.shared.database.EbbingDatabase
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.domain.AutoBackupManager
import com.tgyuu.shared.domain.DailyAlarmScheduler
import com.tgyuu.shared.platform.NotificationScheduler
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.default_alarm_message
import ebbingplanner.shared.generated.resources.notification_content_title
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.getString
import com.tgyuu.shared.domain.model.ErrorBus
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ErrorRepository
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.FeatureFlagRepository
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

    // 태그·반복 주기 최근 사용순 저장소
    single { UsageOrderStore(settings = get()) }

    // Repository
    single<TodoRepository> {
        TodoRepositoryImpl(
            todoTagsDao = get(),
            todoSchedulesDao = get(),
            todoWithSchedulesDao = get(),
            repeatCyclesDao = get(),
            usageOrderStore = get(),
        )
    }

    single<ConfigRepository> {
        ConfigRepositoryImpl(settings = get(), usageOrderStore = get())
    }

    single<ErrorDataSource> { DebugErrorDataSource() }

    single<ErrorRepository> { ErrorRepositoryImpl(errorDataSource = get()) }

    single { ErrorBus(get()) }

    // SyncDataSource는 플랫폼 모듈에서 제공 (iOS=Supabase/Stub, Android=Stub)

    single<SyncRepository> {
        SyncRepositoryImpl(
            settings = get(),
            syncDataSource = get(),
            syncDao = get(),
            schedulesDao = get(),
            repeatCyclesDao = get(),
            tagsDao = get(),
            errorRepository = get(),
            onLocalDataReplaced = { getKoin().get<AlarmRescheduler>().rescheduleAll() },
        )
    }

    single<AnalyticsHelper> { DebugAnalyticsHelper() }

    single<ExperimentRepository> {
        ExperimentRepositoryImpl(settings = get())
    }

    single<FeatureFlagRepository> { FeatureFlagRepositoryImpl() }

    single {
        AutoBackupManager(
            featureFlagRepository = get(),
            configRepository = get(),
            syncRepository = get(),
            errorRepository = get(),
        )
    }

    // 날짜별 알림 재계산 (Android 발화 시점 결정 로직의 iOS 대체). NotificationScheduler는 플랫폼 모듈 제공.
    single {
        val notificationScheduler = get<NotificationScheduler>()
        AlarmRescheduler(
            todoRepository = get(),
            configRepository = get(),
            scheduler = object : DailyAlarmScheduler {
                override fun schedule(id: Int, title: String, message: String, hour: Int, minute: Int, date: LocalDate) =
                    notificationScheduler.scheduleNotification(id, title, message, hour, minute, date)
                override fun cancel(id: Int) = notificationScheduler.cancelNotification(id)
                override fun cancelAll() = notificationScheduler.cancelAllNotifications()
            },
            titleProvider = { getString(Res.string.notification_content_title) },
            defaultMessageProvider = { getString(Res.string.default_alarm_message) },
            errorRepository = get(),
        )
    }
}

/**
 * Returns all shared modules
 */
fun getSharedModules(): List<Module> = listOf(sharedModule)
