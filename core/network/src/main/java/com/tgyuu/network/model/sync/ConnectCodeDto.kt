package com.tgyuu.network.model.sync

import java.util.Date

data class ConnectCodeDto(
    val uuid: String,
    val connectCode: String,
    val connectCodeExpirationTime: Date,
)
