package com.tgyuu.inappupdate

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
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

    private val installStateUpdatedListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Log.d(TAG, "Flexible update downloaded, completing installation")
                appUpdateManager.completeUpdate()
                appUpdateManager.unregisterListener(installStateUpdatedListener)
            }
            InstallStatus.FAILED -> {
                Log.w(TAG, "Flexible update installation failed")
                appUpdateManager.unregisterListener(installStateUpdatedListener)
            }
            else -> {
                Log.d(TAG, "Install status: ${state.installStatus()}")
            }
        }
    }

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
                        if (updateType == AppUpdateType.FLEXIBLE) {
                            appUpdateManager.registerListener(installStateUpdatedListener)
                        }

                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            activity,
                            AppUpdateOptions.newBuilder(updateType).build(),
                            REQUEST_CODE_UPDATE
                        )
                    } else {
                        Log.w(TAG, "Update type $updateType not allowed, falling back to Play Store")
                        context.openPlayStore()
                    }
                }
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                    // 진행 중인 업데이트는 항상 IMMEDIATE로 재개
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            activity,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                            REQUEST_CODE_UPDATE
                        )
                    } else {
                        Log.w(TAG, "Cannot resume in-progress update, falling back to Play Store")
                        context.openPlayStore()
                    }
                }
                else -> {
                    Log.d(TAG, "No update available, opening Play Store")
                    context.openPlayStore()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request update", e)
            context.openPlayStore()
        }
    }

    companion object {
        private const val TAG = "InAppUpdateManager"
        const val REQUEST_CODE_UPDATE = 1001
    }
}
