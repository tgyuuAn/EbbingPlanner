package com.tgyuu.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.model.SortType

fun SortType.toDisplayName(resourceProvider: ResourceProvider): String = when (this) {
    SortType.CREATED -> resourceProvider.getString(R.string.sort_created)
    SortType.NAME -> resourceProvider.getString(R.string.sort_name)
    SortType.PRIORITY -> resourceProvider.getString(R.string.sort_priority)
}

@Composable
fun SortType.displayName(): String = when (this) {
    SortType.CREATED -> stringResource(R.string.sort_created)
    SortType.NAME -> stringResource(R.string.sort_name)
    SortType.PRIORITY -> stringResource(R.string.sort_priority)
}
