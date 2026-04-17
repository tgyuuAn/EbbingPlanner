package com.tgyuu.shared.di

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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * iOS-specific Koin module - provides database instance and platform services
 */
val iosModule = module {
    single<EbbingDatabase> { createEbbingDatabase() }
    single { Settings() }
    single { InAppReviewManager() }
}

/**
 * Returns all iOS modules including shared
 */
fun getIosModules() = listOf(iosModule) + getSharedModules()

/**
 * Initialize Koin for iOS with Firebase integration.
 * Call from Swift:
 *   IosModuleKt.doInitKoin()
 *
 * For Firebase integration, call initKoinWithFirebase() instead.
 */
fun initKoin() {
    startKoin {
        modules(getIosModules())
    }
}

/**
 * Initialize Koin with Firebase services.
 * Call from Swift after FirebaseApp.configure():
 *
 *   IosModuleKt.doInitKoinWithFirebase(
 *       onLogError: { msg in FirebaseErrorBridge.shared.logError(message: msg) },
 *       onSetErrorUserId: { uid in FirebaseErrorBridge.shared.setUserId(uid) },
 *       onClearErrorUserId: { FirebaseErrorBridge.shared.clearUserId() },
 *       onLogAnalyticsEvent: { name, params in FirebaseAnalyticsBridge.shared.logEvent(name: name, parameters: params as? [String: Any]) },
 *       onSetAnalyticsUserId: { uid in FirebaseAnalyticsBridge.shared.setUserId(uid) }
 *   )
 */
fun initKoinWithFirebase(
    onLogError: (String) -> Unit,
    onSetErrorUserId: (String) -> Unit,
    onClearErrorUserId: () -> Unit,
    onLogAnalyticsEvent: (String, Map<String, Any?>) -> Unit,
    onSetAnalyticsUserId: (String?) -> Unit,
) {
    startKoin {
        modules(
            getIosModules() + module {
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
