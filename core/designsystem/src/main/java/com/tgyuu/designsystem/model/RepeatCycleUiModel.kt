package com.tgyuu.designsystem.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class RepeatCycleUiModel(
    val id: Int,
    val intervals: ImmutableList<Int>,
    val displayName: String,
)
