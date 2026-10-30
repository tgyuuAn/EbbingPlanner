package com.tgyuu.shared.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory
import kotlinx.cinterop.ExperimentalForeignApi

private const val APP_GROUP_ID = "group.com.tgyuu.ebbingplanner"

fun createEbbingDatabase(): EbbingDatabase {
    val dbFilePath = getDatabasePath()
    return Room.databaseBuilder<EbbingDatabase>(
        name = dbFilePath,
    )
        .setDriver(BundledSQLiteDriver())
        .addMigrations(DatabaseMigrations.MIGRATION_4_TO_5)
        .build()
}

/**
 * DB 파일 경로. 위젯 확장이 App Group 컨테이너의 DB를 읽으므로 App Group을 우선 사용한다.
 *
 * 과거 버전은 Documents에 DB가 있으면 계속 Documents를 써서 기존 사용자의 위젯이 비어 보였다.
 * App Group에 DB가 없고 Documents에만 있으면, 열기 전에 1회 App Group으로 이동한다(-wal/-shm 포함).
 * 이동에 실패하면 데이터 유실을 막기 위해 Documents를 그대로 사용한다.
 */
@OptIn(ExperimentalForeignApi::class)
private fun getDatabasePath(): String {
    val fileManager = NSFileManager.defaultManager
    val documentsPath = NSHomeDirectory() + "/Documents/${EbbingDatabase.NAME}"

    val containerPath = fileManager
        .containerURLForSecurityApplicationGroupIdentifier(APP_GROUP_ID)
        ?.path
        ?: return documentsPath
    val appGroupPath = "$containerPath/${EbbingDatabase.NAME}"

    if (fileManager.fileExistsAtPath(appGroupPath)) return appGroupPath
    if (!fileManager.fileExistsAtPath(documentsPath)) return appGroupPath

    return if (migrateDatabaseFiles(fileManager, documentsPath, appGroupPath)) appGroupPath else documentsPath
}

@OptIn(ExperimentalForeignApi::class)
private fun migrateDatabaseFiles(fileManager: NSFileManager, fromBase: String, toBase: String): Boolean {
    val suffixes = listOf("", "-wal", "-shm")
    val moved = mutableListOf<String>()
    for (suffix in suffixes) {
        val from = fromBase + suffix
        if (!fileManager.fileExistsAtPath(from)) continue
        val to = toBase + suffix
        if (fileManager.fileExistsAtPath(to)) fileManager.removeItemAtPath(to, error = null)
        if (fileManager.moveItemAtPath(from, toPath = to, error = null)) {
            moved += suffix
        } else {
            // 일부만 옮겨지면 WAL 불일치로 손상될 수 있으므로 되돌리고 Documents를 계속 사용
            moved.forEach { s -> fileManager.moveItemAtPath(toBase + s, toPath = fromBase + s, error = null) }
            return false
        }
    }
    return true
}
