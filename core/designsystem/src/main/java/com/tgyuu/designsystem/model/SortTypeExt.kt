package com.tgyuu.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.tgyuu.common.ui.resource.ResourceProvider
import com.tgyuu.designsystem.R
import com.tgyuu.domain.model.SortType

fun SortType.toDisplayName(resourceProvider: ResourceProvider): String = when (this) {
    SortType.CREATED -> resourceProvider.getString(R.string.sort_latest)
    SortType.BY_TAG -> resourceProvider.getString(R.string.sort_by_tag)
}

@Composable
fun SortType.displayName(): String = when (this) {
    SortType.CREATED -> stringResource(R.string.sort_latest)
    SortType.BY_TAG -> stringResource(R.string.sort_by_tag)
}
