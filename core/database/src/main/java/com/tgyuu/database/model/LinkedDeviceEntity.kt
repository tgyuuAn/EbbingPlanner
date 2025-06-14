package com.tgyuu.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tgyuu.domain.model.LinkedDevice
import java.time.LocalDate

@Entity(tableName = "linked_device")
data class LinkedDeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uuid: String,
    val lastUsedAt: LocalDate = LocalDate.now(),
)

fun LinkedDevice.toEntity() = LinkedDeviceEntity(
    id = this.id,
    uuid = this.uuid,
    lastUsedAt = this.lastUsedAt,
)
