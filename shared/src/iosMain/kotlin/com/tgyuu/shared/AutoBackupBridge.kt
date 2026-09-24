package com.tgyuu.shared

import com.tgyuu.shared.domain.AutoBackupManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

private val autoBackupScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

/** 앱이 백그라운드로 진입할 때 Swift(SceneDelegate/scenePhase)에서 호출 */
fun handleAppDidEnterBackground() {
    val manager = KoinPlatform.getKoin().get<AutoBackupManager>()
    autoBackupScope.launch { manager.onAppStop() }
}

/** 앱이 포그라운드로 복귀할 때 Swift에서 호출 (실패한 백업 재시도) */
fun handleAppWillEnterForeground() {
    val manager = KoinPlatform.getKoin().get<AutoBackupManager>()
    autoBackupScope.launch { manager.tryPendingBackup() }
}
