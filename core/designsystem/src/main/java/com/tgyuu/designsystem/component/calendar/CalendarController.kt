package com.tgyuu.designsystem.component.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.LocalDate

@Composable
internal fun CalendarController(
    currentDate: LocalDate,
    onGotoTodayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .semantics { contentDescription = "달력 컨트롤러" },
    ) {
        IconButton(onClick = onGotoTodayClick) {
            if (currentDate != LocalDate.now()) {
                Image(
                    painter = painterResource(R.drawable.ic_return),
                    contentDescription = "이전 달",
                    colorFilter = ColorFilter.tint(EbbingTheme.colors.black),
                    modifier = Modifier.size(16.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(16.dp))
            }
        }

        Text(
            text = "${currentDate.year}년 ${currentDate.monthValue}월",
            textAlign = TextAlign.Center,
            style = EbbingTheme.typography.headingSSB,
            color = EbbingTheme.colors.black,
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}
