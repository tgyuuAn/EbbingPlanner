package com.tgyuu.shared.designsystem.component.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.ic_textinput_check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EbbingModalBottomSheet(
    sheetState: EbbingBottomSheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    // Use passed content or fall back to sheetState.content
    val actualContent = content ?: sheetState.content

    if (sheetState.isVisible && actualContent != null) {
        // Trigger animation after ModalBottomSheet enters composition
        LaunchedEffect(Unit) {
            sheetState.state.show()
        }

        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState.state,
            containerColor = EbbingTheme.colors.background,
            contentColor = EbbingTheme.colors.black,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = {
                Spacer(modifier = Modifier.height(28.dp))
            },
            modifier = modifier,
        ) {
            Column(modifier = Modifier.navigationBarsPadding()) {
                actualContent()
            }
        }
    }
}

@Composable
fun EbbingBottomSheetHeader(
    title: String,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    rightComponent: (@Composable () -> Unit)? = null,
) {
    // Android EbbingBottomSheetHeader와 동일: 내부 수평 패딩 없음(호출부 Column이 20dp 제공).
    // 기존엔 내부에 20dp가 있어 호출부 20dp와 합쳐져 헤더만 40dp로 밀려 있었음.
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
                color = EbbingTheme.colors.dark3,
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
        // Android EbbingBottomSheetListItemDefault와 동일: 내부 수평 패딩 없음(호출부가 20dp 제공).
        // 기존엔 내부 20dp가 있어 호출부 20dp와 합쳐져 목록 항목만 헤더보다 더 밀려 있었음.
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

        Text(
            text = label,
            style = EbbingTheme.typography.bodyMM,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (enabled && checked) {
            Icon(
                painter = painterResource(Res.drawable.ic_textinput_check),
                contentDescription = null,
                tint = EbbingTheme.colors.primaryDefault,
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
