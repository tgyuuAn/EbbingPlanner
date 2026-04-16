package com.tgyuu.setting.graph.ui.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.setting.graph.main.contract.AlarmMessageBottomSheetState

@Composable
fun AlarmMessageBottomSheet(
    state: AlarmMessageBottomSheetState,
    onMessageChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(
            title = "알림 메시지 설정",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryNormal, fontWeight = FontWeight.Bold)) {
                    append("{할일}")
                }
                append("은 할 일 제목으로 자동 변환됩니다 (최대 1번)")
            },
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.textDisabled,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        EbbingTextInputDefault(
            value = state.message,
            onValueChange = onMessageChange,
            hint = "알림 메시지를 입력하세요",
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = state.errorMessage,
                style = EbbingTheme.typography.caption12R,
                color = EbbingTheme.colors.statusError,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = state.lengthText,
                style = EbbingTheme.typography.caption12R,
                color = if (state.isValidLength) EbbingTheme.colors.textDisabled else EbbingTheme.colors.statusError,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isValidPlaceholder && state.previewMessage.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EbbingTheme.colors.fillNormal,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = "미리보기",
                    style = EbbingTheme.typography.caption12R,
                    color = EbbingTheme.colors.textDisabled,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = state.previewMessage,
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textSub,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.shouldShowResetButton) {
            Text(
                text = "기본값으로 복원",
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.primaryNormal,
                modifier = Modifier
                    .clickable { onResetClick() }
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        EbbingSolidButton(
            label = "적용",
            onClick = onUpdateClick,
            enabled = state.canApply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )
    }
}
