package com.tgyuu.shared.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class RepeatCycleUiModel(
    val id: Int,
    val intervals: ImmutableList<Int> = persistentListOf(),
    val displayName: String,
)
