package com.tgyuu.shared.designsystem.model

import androidx.compose.runtime.Composable
import com.tgyuu.shared.domain.model.SortType
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.sort_by_tag
import ebbingplanner.shared.generated.resources.sort_created
import org.jetbrains.compose.resources.stringResource

@Composable
fun SortType.displayName(): String = when (this) {
    SortType.CREATED -> stringResource(Res.string.sort_created)
    SortType.BY_TAG -> stringResource(Res.string.sort_by_tag)
}
