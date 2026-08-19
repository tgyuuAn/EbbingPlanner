package com.tgyuu.shared.di

import com.tgyuu.shared.data.source.StubSyncDataSource
import com.tgyuu.shared.data.source.SupabaseSyncDataSource
import com.tgyuu.shared.data.source.SyncDataSource
import com.tgyuu.shared.database.EbbingDatabase
import com.tgyuu.shared.database.createEbbingDatabase
import com.tgyuu.shared.platform.InAppReviewManager
import com.tgyuu.shared.platform.Settings
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.platform.ErrorDataSource
import com.tgyuu.shared.platform.FirebaseAnalyticsHelper
import com.tgyuu.shared.platform.FirebaseErrorDataSource
import com.tgyuu.shared.database.dao.RepeatCyclesDao
import com.tgyuu.shared.database.dao.TodoSchedulesDao
import com.tgyuu.shared.database.dao.TodoTagsDao
import com.tgyuu.shared.database.dao.TodoWithSchedulesDao
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * iOS-specific Koin module - provides database, platform services, and Supabase sync.
 *
 * @param supabaseUrl / supabaseKey: Info.plist에서 읽어 Swift가 전달. 비어 있으면 Stub으로 폴백.
 */
fun iosModule(supabaseUrl: String, supabaseKey: String) = module {
    single<EbbingDatabase> { createEbbingDatabase() }
    single { Settings() }
    single { InAppReviewManager() }
    single { com.tgyuu.shared.platform.NotificationScheduler() }

    if (supabaseUrl.isNotBlank() && supabaseKey.isNotBlank()) {
        single<SupabaseClient> {
            createSupabaseClient(supabaseUrl = supabaseUrl, supabaseKey = supabaseKey) {
                install(Postgrest)
            }
        }
        single<SyncDataSource> { SupabaseSyncDataSource(get()) }
    } else {
        single<SyncDataSource> { StubSyncDataSource() }
    }
}

/**
 * Returns all iOS modules including shared
 */
fun getIosModules(supabaseUrl: String, supabaseKey: String) =
    listOf(iosModule(supabaseUrl, supabaseKey)) + getSharedModules()

/**
 * Initialize Koin for iOS.
 * Call from Swift:
 *   IosModuleKt.doInitKoin(supabaseUrl:..., supabaseKey:...)
 */
fun initKoin(supabaseUrl: String, supabaseKey: String) {
    startKoin {
        modules(getIosModules(supabaseUrl, supabaseKey))
    }
}

/**
 * Initialize Koin with Firebase services. Call from Swift after FirebaseApp.configure().
 */
fun initKoinWithFirebase(
    onLogError: (String) -> Unit,
    onSetErrorUserId: (String) -> Unit,
    onClearErrorUserId: () -> Unit,
    onLogAnalyticsEvent: (String, Map<String, Any?>) -> Unit,
    onSetAnalyticsUserId: (String?) -> Unit,
    supabaseUrl: String,
    supabaseKey: String,
) {
    startKoin {
        modules(
            getIosModules(supabaseUrl, supabaseKey) + module {
                // Override default debug implementations with Firebase
                single<ErrorDataSource> {
                    FirebaseErrorDataSource(
                        onLogError = onLogError,
                        onSetUserId = onSetErrorUserId,
                        onClearUserId = onClearErrorUserId,
                    )
                }
                single<AnalyticsHelper> {
                    FirebaseAnalyticsHelper(
                        onLogEvent = onLogAnalyticsEvent,
                        onSetUserId = onSetAnalyticsUserId,
                    )
                }
            }
        )
    }
}

/**
 * Koin Helper for iOS - provides access to DAOs from Swift
 */
class KoinHelper : KoinComponent {
    val schedulesDao: TodoSchedulesDao by inject()
    val tagsDao: TodoTagsDao by inject()
    val repeatCyclesDao: RepeatCyclesDao by inject()
    val todoWithSchedulesDao: TodoWithSchedulesDao by inject()
}
