package com.tgyuu.shared.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.util.throttledClickable

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
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(EbbingTheme.colors.light3)
            // 토글 전체 영역을 클릭하면 반대편 값으로 전환
            .throttledClickable(300L) { onSelectedChange(!selectedFirst) }
            .padding(2.dp),
    ) {
        ToggleSegment(
            label = firstLabel,
            selected = selectedFirst,
        )
        ToggleSegment(
            label = secondLabel,
            selected = !selectedFirst,
        )
    }
}

@Composable
private fun ToggleSegment(
    label: String,
    selected: Boolean,
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
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label,
            style = EbbingTheme.typography.bodySSB,
            color = if (selected) EbbingTheme.colors.black else EbbingTheme.colors.dark1,
        )
    }
}
