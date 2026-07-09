package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.ebbingAnimateColorAsState
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

/**
 * 사각형(모서리 라운드 6dp) 체크박스.
 * - 선택 시: primaryNormal 채움 + 흰 체크
 * - 미선택 시: fillNormal 배경 + strokeOutline 1dp 테두리
 *
 * 원형 완료 체크는 [EbbingCheck] 를 사용한다.
 */
@Composable
fun EbbingCheckBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(6.dp)
    val boxColor = ebbingAnimateColorAsState(
        targetValue = if (checked) EbbingTheme.colors.primaryNormal else EbbingTheme.colors.fillNormal
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(24.dp)
            .clip(shape)
            .background(boxColor)
            .then(
                if (checked) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = EbbingTheme.colors.strokeOutline,
                        shape = shape,
                    )
                }
            )
            .clickable { onCheckedChange(!checked) },
    ) {
        if (checked) {
            Image(
                painter = painterResource(R.drawable.ic_check),
                colorFilter = ColorFilter.tint(EbbingTheme.colors.background),
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 7.dp),
            )
        }
    }
}

@EbbingPreview
@Composable
private fun PreviewCheckBox() {
    BasePreview {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EbbingCheckBox(
                checked = true,
                onCheckedChange = {},
            )

            EbbingCheckBox(
                checked = false,
                onCheckedChange = {},
            )
        }
    }
}
