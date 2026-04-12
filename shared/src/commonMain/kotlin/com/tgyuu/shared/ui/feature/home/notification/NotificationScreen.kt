package com.tgyuu.shared.ui.feature.home.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.component.EbbingTextInputDefault
import com.tgyuu.shared.designsystem.component.EbbingToggle
import com.tgyuu.shared.designsystem.foundation.EbbingTheme

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        EbbingSubTopBar(
            title = "알림 설정",
            onNavigationClick = { viewModel.onIntent(NotificationIntent.OnBackClick) },
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(20.dp),
        ) {
            // Notification toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "알림",
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.black,
                    modifier = Modifier.weight(1f),
                )

                EbbingToggle(
                    checked = state.isNotificationEnabled,
                    onCheckedChange = { viewModel.onIntent(NotificationIntent.OnNotificationToggle(it)) },
                )
            }

            HorizontalDivider(
                color = EbbingTheme.colors.light2,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // Alarm time
            Text(
                text = "알림 시간",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = state.formattedAlarmTime,
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.primaryDefault,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { viewModel.onIntent(NotificationIntent.OnTimePickerClick) },
            )

            HorizontalDivider(
                color = EbbingTheme.colors.light2,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // Alarm message
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "알림 메시지",
                    style = EbbingTheme.typography.bodyMSB,
                    color = EbbingTheme.colors.black,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = "초기화",
                    style = EbbingTheme.typography.bodySM,
                    color = EbbingTheme.colors.dark3,
                    modifier = Modifier.clickable {
                        viewModel.onIntent(NotificationIntent.OnResetMessage)
                    },
                )
            }

            EbbingTextInputDefault(
                value = state.alarmMessage,
                hint = "알림 메시지를 입력하세요",
                onValueChange = { viewModel.onIntent(NotificationIntent.OnMessageChange(it)) },
                limit = 100,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            )

            Text(
                text = "{할일}을 사용하면 일정 이름으로 자동 대체됩니다",
                style = EbbingTheme.typography.bodySM,
                color = EbbingTheme.colors.dark3,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )

            HorizontalDivider(
                color = EbbingTheme.colors.light2,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // Preview
            Text(
                text = "미리보기",
                style = EbbingTheme.typography.bodyMSB,
                color = EbbingTheme.colors.black,
            )

            Text(
                text = state.previewMessage,
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                modifier = Modifier.padding(top = 8.dp),
            )

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
