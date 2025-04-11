package com.tgyuu.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
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
    subTitle: String? = null,
) {
    Text(
        text = title,
        style = EbbingTheme.typography.headingLSB,
        color = EbbingTheme.colors.black,
        modifier = Modifier.padding(top = 10.dp),
    )

    subTitle?.let {
        Text(
            text = subTitle,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark2,
            modifier = Modifier.padding(vertical = 8.dp),
        )
    }
}
