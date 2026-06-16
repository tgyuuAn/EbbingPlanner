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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
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
            title = stringResource(R.string.setting_alarm_message_setting),
            modifier = Modifier.padding(bottom = 16.dp),
        )

        val placeholderToken = stringResource(R.string.setting_alarm_message_placeholder_token)
        val placeholderGuide = stringResource(R.string.setting_alarm_message_placeholder_guide)
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = EbbingTheme.colors.primaryNormal, fontWeight = FontWeight.Bold)) {
                    append(placeholderToken)
                }
                append(placeholderGuide)
            },
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.textDisabled,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        EbbingTextInputDefault(
            value = state.message,
            onValueChange = onMessageChange,
            hint = stringResource(R.string.setting_alarm_message_hint),
            modifier = Modifier.fillMaxWidth(),
        )

        val errorMessage = when {
            state.placeholderCount > 1 ->
                stringResource(R.string.setting_alarm_message_error_placeholder)

            !state.isValidLength ->
                stringResource(R.string.setting_alarm_message_error_length)

            else -> ""
        }
        val lengthText = stringResource(R.string.setting_alarm_message_length, state.message.length)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = errorMessage,
                style = EbbingTheme.typography.caption12R,
                color = EbbingTheme.colors.statusError,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = lengthText,
                style = EbbingTheme.typography.caption12R,
                color = if (state.isValidLength) EbbingTheme.colors.textDisabled else EbbingTheme.colors.statusError,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val previewSampleWord = stringResource(R.string.setting_alarm_message_preview_sample)
        val previewMessage = when (state.placeholderCount) {
            1 -> state.message.replace(
                AlarmMessageBottomSheetState.placeholderToken,
                previewSampleWord,
            )
            0 -> state.message
            else -> ""
        }

        if (state.isValidPlaceholder && previewMessage.isNotEmpty()) {
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
                    text = stringResource(R.string.setting_preview),
                    style = EbbingTheme.typography.caption12R,
                    color = EbbingTheme.colors.textDisabled,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
                Text(
                    text = previewMessage,
                    style = EbbingTheme.typography.body16M,
                    color = EbbingTheme.colors.textSub,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.shouldShowResetButton) {
            Text(
                text = stringResource(R.string.setting_restore_default),
                style = EbbingTheme.typography.body16M,
                color = EbbingTheme.colors.primaryNormal,
                modifier = Modifier
                    .clickable { onResetClick() }
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        EbbingSolidButton(
            label = stringResource(R.string.setting_apply),
            onClick = onUpdateClick,
            enabled = state.canApply,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        )
    }
}
