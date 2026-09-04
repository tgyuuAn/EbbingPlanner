package com.tgyuu.shared.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSFileManager
import platform.Foundation.NSHomeDirectory

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
 * Returns the database file path.
 * Uses Documents directory as the primary location.
 * If App Group is available AND Documents doesn't have a DB yet, uses App Group for widget sharing.
 */
private fun getDatabasePath(): String {
    val documentsPath = NSHomeDirectory() + "/Documents/${EbbingDatabase.NAME}"

    // If DB already exists in Documents, keep using it (migration safety)
    val fileManager = NSFileManager.defaultManager
    if (fileManager.fileExistsAtPath(documentsPath)) {
        return documentsPath
    }

    // Try App Group for new installs (widget sharing)
    val containerUrl = fileManager.containerURLForSecurityApplicationGroupIdentifier(APP_GROUP_ID)
    if (containerUrl != null) {
        return containerUrl.path + "/${EbbingDatabase.NAME}"
    }

    // Fallback to Documents
    return documentsPath
}
