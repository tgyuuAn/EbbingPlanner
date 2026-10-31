package com.tgyuu.shared.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.common.util.noRippleClickable
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import com.tgyuu.shared.designsystem.util.EbbingVisibleAnimation
import com.tgyuu.shared.platform.NetworkState
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.sync_network_connect_action
import ebbingplanner.shared.generated.resources.sync_network_connected
import ebbingplanner.shared.generated.resources.sync_network_disconnected
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

/**
 * 네트워크 상태 배너 (Android feature/sync NetworkBanner 포팅).
 * 네트워크가 필요한 화면(동기화·복원)에서만 보이고, 끊기면 빨간 배너, 다시 연결되면 2초간 초록 배너.
 *
 * @param onConnectClick '연결하기' (Android: 무선 설정, iOS: 앱 설정)
 */
@Composable
fun NetworkBanner(
    needsNetwork: Boolean,
    networkState: NetworkState,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showConnected by remember { mutableStateOf(false) }
    var previousState by remember { mutableStateOf<NetworkState?>(null) }

    LaunchedEffect(networkState) {
        if (previousState == NetworkState.NotConnected && networkState == NetworkState.Connected) {
            showConnected = true
            delay(2000)
            showConnected = false
        }

        previousState = networkState
    }

    Box(modifier) {
        EbbingVisibleAnimation(
            visible = needsNetwork && networkState != NetworkState.Connected,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(60.dp)
                    .background(EbbingTheme.colors.statusError)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.sync_network_disconnected),
                    // Android heading14SB(14sp SemiBold)
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.textOnPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(Res.string.sync_network_connect_action),
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.textOnPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .noRippleClickable(onClick = onConnectClick),
                )
            }
        }

        EbbingVisibleAnimation(
            visible = needsNetwork && showConnected,
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(60.dp)
                    .background(EbbingTheme.colors.statusSuccess)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.sync_network_connected),
                    style = EbbingTheme.typography.bodySSB,
                    color = EbbingTheme.colors.textOnPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
