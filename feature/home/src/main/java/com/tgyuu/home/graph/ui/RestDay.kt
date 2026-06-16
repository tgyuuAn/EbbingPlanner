package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingChip
import com.tgyuu.designsystem.component.calendar.toShortLabel
import com.tgyuu.designsystem.foundation.EbbingTheme
import kotlinx.collections.immutable.ImmutableSet
import java.time.DayOfWeek

@Composable
internal fun RestDayContent(
    restDays: ImmutableSet<DayOfWeek>,
    onRestDayChange: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.home_rest_day),
        style = EbbingTheme.typography.body16M,
        color = EbbingTheme.colors.textOnBackground,
        modifier = Modifier.padding(top = 32.dp),
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        DayOfWeek.entries.forEach {
            EbbingChip(
                label = it.toShortLabel(),
                selected = it in restDays,
                onChipClicked = { onRestDayChange(it) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}
