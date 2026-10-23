package com.tgyuu.shared.database

import android.content.Context
import androidx.room.Room

fun createEbbingDatabase(context: Context): EbbingDatabase {
    return Room.databaseBuilder<EbbingDatabase>(
        context = context,
        name = context.getDatabasePath(EbbingDatabase.NAME).absolutePath,
    )
        .addMigrations(DatabaseMigrations.MIGRATION_4_TO_5)
        .build()
}
