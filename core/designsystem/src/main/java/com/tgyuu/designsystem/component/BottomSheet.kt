package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
fun EbbingModalBottomSheet(
    sheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
    sheetContent: @Composable (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetGesturesEnabled = false,
        sheetContentColor = EbbingTheme.colors.background,
        sheetBackgroundColor = EbbingTheme.colors.background,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetState = sheetState,
        sheetContent = {
            Column(modifier = Modifier.navigationBarsPadding()) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                )

                sheetContent?.invoke()
            }
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        content()
    }
}

@Composable
fun EbbingBottomSheetHeader(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    rightComponent: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 10.dp),
            )

            rightComponent?.invoke()
        }

        subTitle?.let {
            Text(
                text = subTitle,
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.dark2,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
fun EbbingBottomSheetListItemDefault(
    label: String,
    checked: Boolean,
    onChecked: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Int? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .clickable(enabled = enabled) { onChecked() },
    ) {
        if (color != null) {
            Spacer(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .then(
                        if (checked) {
                            Modifier.border(
                                width = 1.dp,
                                color = EbbingTheme.colors.primaryDefault,
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    ),
            )
        }

        val textColor = if (!enabled) EbbingTheme.colors.dark3
        else if (checked) EbbingTheme.colors.primaryDefault
        else EbbingTheme.colors.black

        BasicText(
            text = label,
            style = if (checked) EbbingTheme.typography.bodyMSB else EbbingTheme.typography.bodyMM,
            color = { textColor },
            autoSize = TextAutoSize.StepBased(
                minFontSize = 8.sp,
                maxFontSize = 16.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (enabled && checked) {
            Image(
                painter = painterResource(R.drawable.ic_textinput_check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = EbbingTheme.colors.primaryDefault),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp),
            )
        } else {
            Spacer(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(32.dp),
            )
        }
    }
}
