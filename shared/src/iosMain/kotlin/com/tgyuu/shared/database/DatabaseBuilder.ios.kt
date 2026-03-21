package com.tgyuu.shared.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

fun createEbbingDatabase(): EbbingDatabase {
    val dbFilePath = NSHomeDirectory() + "/Documents/${EbbingDatabase.NAME}"
    return Room.databaseBuilder<EbbingDatabase>(
        name = dbFilePath,
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}
