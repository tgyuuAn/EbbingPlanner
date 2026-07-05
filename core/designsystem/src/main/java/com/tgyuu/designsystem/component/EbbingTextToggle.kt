package com.tgyuu.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.throttledClickable
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.EbbingPreview
import com.tgyuu.designsystem.foundation.EbbingTheme

/**
 * 두 개의 라벨 중 하나를 선택하는 세그먼트형 텍스트 토글.
 *
 * 디자인(Figma 958-12466)의 Text_Toggle 컴포넌트를 반영한다.
 * 홈 화면의 월/주 전환, 최신순/태그별 전환 등에 재사용한다.
 *
 * @param selectedFirst true 면 [firstLabel], false 면 [secondLabel] 이 선택된 상태
 * @param onSelectedChange 세그먼트를 탭했을 때 선택된 값(첫 번째=true) 을 전달
 */
@Composable
fun EbbingTextToggle(
    firstLabel: String,
    secondLabel: String,
    selectedFirst: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(EbbingTheme.colors.fillTextfield)
            .padding(2.dp),
    ) {
        ToggleSegment(
            label = firstLabel,
            selected = selectedFirst,
            onClick = { onSelectedChange(true) },
        )
        ToggleSegment(
            label = secondLabel,
            selected = !selectedFirst,
            onClick = { onSelectedChange(false) },
        )
    }
}

@Composable
private fun ToggleSegment(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier.shadow(elevation = 8.dp, shape = shape, spotColor = Color(0x338994A8))
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(if (selected) EbbingTheme.colors.background else Color.Transparent)
            .throttledClickable(300L) { if (!selected) onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = EbbingTheme.typography.heading14SB,
            color = if (selected) EbbingTheme.colors.textOnBackground else EbbingTheme.colors.textSub,
        )
    }
}

@EbbingPreview
@Composable
private fun PreviewTextToggle() {
    BasePreview {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(10.dp),
        ) {
            EbbingTextToggle(
                firstLabel = "월",
                secondLabel = "주",
                selectedFirst = true,
                onSelectedChange = {},
            )
            EbbingTextToggle(
                firstLabel = "최신순",
                secondLabel = "태그별",
                selectedFirst = false,
                onSelectedChange = {},
            )
        }
    }
}
