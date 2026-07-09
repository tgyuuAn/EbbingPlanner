package com.tgyuu.domain.model.sync

sealed interface RestoreResult {
    data class Success(val deviceName: String) : RestoreResult
    data object NotFound : RestoreResult
    data object EmptyData : RestoreResult
    data object Ambiguous : RestoreResult
    data object SelfDevice : RestoreResult
}
