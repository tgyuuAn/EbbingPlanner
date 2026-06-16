package com.tgyuu.sync.network

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.R
import kotlinx.coroutines.delay

@Composable
fun NetworkBanner(
    needsNetwork: Boolean,
    networkState: NetworkState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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
            modifier = Modifier.align(Alignment.BottomCenter)
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
                    text = stringResource(R.string.sync_network_disconnected),
                    style = EbbingTheme.typography.heading14SB,
                    color = EbbingTheme.colors.textOnPrimary,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.sync_network_connect_action),
                    style = EbbingTheme.typography.heading14SB,
                    color = EbbingTheme.colors.textOnPrimary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable {
                            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            context.startActivity(intent)
                        },
                )
            }
        }

        EbbingVisibleAnimation(
            visible = needsNetwork && showConnected,
            modifier = Modifier.align(Alignment.BottomCenter)
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
                    text = stringResource(R.string.sync_network_connected),
                    style = EbbingTheme.typography.heading14SB,
                    color = EbbingTheme.colors.textOnPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
