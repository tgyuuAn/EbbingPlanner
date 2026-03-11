package com.tgyuu.inappupdate

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.tgyuu.common.util.openPlayStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InAppUpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val appUpdateManager = AppUpdateManagerFactory.create(context)

    suspend fun requestUpdate(activity: Activity, isImmediateUpdate: Boolean) {
        try {
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            val updateType = if (isImmediateUpdate) {
                AppUpdateType.IMMEDIATE
            } else {
                AppUpdateType.FLEXIBLE
            }

            when (appUpdateInfo.updateAvailability()) {
                UpdateAvailability.UPDATE_AVAILABLE -> {
                    if (appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            activity,
                            AppUpdateOptions.newBuilder(updateType).build(),
                            REQUEST_CODE_UPDATE
                        )
                    } else {
                        context.openPlayStore()
                    }
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(updateType).build(),
                        REQUEST_CODE_UPDATE
                    )
                }
                else -> {
                    context.openPlayStore()
                }
            }
        } catch (e: Exception) {
            context.openPlayStore()
        }
    }

    companion object {
        const val REQUEST_CODE_UPDATE = 1001
    }
}
