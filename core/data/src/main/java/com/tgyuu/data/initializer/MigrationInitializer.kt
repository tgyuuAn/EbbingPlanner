package com.tgyuu.data.initializer

import android.content.Context
import com.tgyuu.common.initializer.Initializer
import com.tgyuu.common.initializer.Initializer.Companion.PRIORITY_HIGH
import com.tgyuu.domain.repository.ConfigRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MigrationInitializer @Inject constructor(
    private val configRepository: ConfigRepository,
    @ApplicationContext private val context: Context,
) : Initializer {
    override val priority: Int = PRIORITY_HIGH

    override suspend fun initialize() {
        val currentVersionCode = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .longVersionCode.toInt()
        val lastVersion = configRepository.getLastVersion()

        if (lastVersion < currentVersionCode) {
            runMigrations(from = lastVersion, to = currentVersionCode)
            configRepository.setLastVersion(currentVersionCode)
        }
    }

    private suspend fun runMigrations(from: Int, to: Int) {
        // from == 0: 마이그레이션 시스템 도입 이전 유저 (기존 유저)
        // 향후 마이그레이션 추가 예시:
        // if (from < 32) { ... }
    }
}
