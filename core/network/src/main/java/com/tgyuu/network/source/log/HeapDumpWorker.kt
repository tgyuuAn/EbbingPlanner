package com.tgyuu.network.source.log

import android.content.Context
import android.os.Debug
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.io.File

@HiltWorker
class HeapDumpWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val hprofStorageRef = Firebase.storage.reference.child(HPROF_DIR)

        // WorkManager 파라미터 얻기
        val timestamp = inputData.getLong(KEY_START_TIME, System.currentTimeMillis())
        val fileName = "oom_$timestamp.hprof"
        val file = File(appContext.filesDir, fileName)

        try {
            // 이미 파일이 존재하면 재생성하지 않고 업로드만 시도
            if (!file.exists()) {
                Debug.dumpHprofData(file.absolutePath)
            }

            // Firebase Cloud Storage 업로드
            val storageRef = hprofStorageRef.child("$HPROF_DIR/$fileName")
            storageRef.putFile(file.toUri()).await()

            // 성공하면 디바이스에 파일 제거
            file.delete()
            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        internal const val HPROF_DIR = "hprofs"
        internal const val KEY_START_TIME = "startTime"
    }
}
