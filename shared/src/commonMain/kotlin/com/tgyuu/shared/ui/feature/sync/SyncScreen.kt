package com.tgyuu.shared.ui.feature.sync

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.sync_screen_title
import ebbingplanner.shared.generated.resources.sync_back
import ebbingplanner.shared.generated.resources.sync_sync
import ebbingplanner.shared.generated.resources.sync_disconnect
import ebbingplanner.shared.generated.resources.sync_no_record
import ebbingplanner.shared.generated.resources.sync_no_record_or_network
import ebbingplanner.shared.generated.resources.sync_generating
import ebbingplanner.shared.generated.resources.sync_device_id_label
import ebbingplanner.shared.generated.resources.sync_device_last_update_label
import ebbingplanner.shared.generated.resources.sync_server_last_update_label
import ebbingplanner.shared.generated.resources.sync_linked_id_label
import ebbingplanner.shared.generated.resources.sync_section_title
import ebbingplanner.shared.generated.resources.sync_do_sync
import ebbingplanner.shared.generated.resources.sync_manual_hint
import ebbingplanner.shared.generated.resources.sync_disconnect_action
import ebbingplanner.shared.generated.resources.sync_connect_action
import ebbingplanner.shared.generated.resources.sync_guide_1
import ebbingplanner.shared.generated.resources.sync_guide_highlight_1
import ebbingplanner.shared.generated.resources.sync_guide_2
import ebbingplanner.shared.generated.resources.sync_guide_3
import ebbingplanner.shared.generated.resources.sync_guide_highlight_2
import ebbingplanner.shared.generated.resources.sync_guide_4
import ebbingplanner.shared.generated.resources.sync_guide_5
import ebbingplanner.shared.generated.resources.sync_confirm_sync_title
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_1
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_highlight_1
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_2
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_highlight_2
import ebbingplanner.shared.generated.resources.sync_confirm_sync_desc_3
import ebbingplanner.shared.generated.resources.sync_confirm_disconnect_title
import ebbingplanner.shared.generated.resources.sync_confirm_disconnect_desc
import org.jetbrains.compose.resources.stringResource

@Composable
fun SyncScreen(
    viewModel: SyncViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var showSyncUpDialog by remember { mutableStateOf(false) }
    var showDisconnectDialog by remember { mutableStateOf(false) }

    if (showSyncUpDialog) {
        SyncUpConfirmDialog(
            onConfirm = {
                viewModel.onIntent(SyncIntent.OnSyncUpClick)
                showSyncUpDialog = false
            },
            onDismiss = { showSyncUpDialog = false },
        )
    }

    if (showDisconnectDialog) {
        DisconnectConfirmDialog(
            onConfirm = {
                viewModel.onIntent(SyncIntent.OnDisconnectClick)
                showDisconnectDialog = false
            },
            onDismiss = { showDisconnectDialog = false },
        )
    }

    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
    val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        EbbingSubTopBar(
            title = stringResource(Res.string.sync_screen_title),
            onNavigationClick = { viewModel.onIntent(SyncIntent.OnBackClick) },
            modifier = Modifier.padding(bottom = 20.dp),
        )

        if (isWide) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f).verticalScroll(scrollState)) {
                    if (state.linkedUuid != null) {
                        LinkedUuidBody(linkedUuid = state.linkedUuid ?: "", lastSyncedAt = state.localLastSyncedAt?.toString() ?: stringResource(Res.string.sync_no_record), lastUpdatedAt = state.serverLastUpdatedAt?.toString() ?: stringResource(Res.string.sync_no_record_or_network))
                    } else {
                        UuidBody(uuid = state.uuid, lastSyncedAt = state.localLastSyncedAt?.toString() ?: stringResource(Res.string.sync_no_record), lastUpdatedAt = state.serverLastUpdatedAt?.toString() ?: stringResource(Res.string.sync_no_record_or_network))
                    }
                    SyncUpBody(
                        isConnected = state.linkedUuid != null,
                        isSyncUpEnabled = state.isSyncUpEnabled,
                        onSyncUpClick = { if (state.isSyncUpEnabled) showSyncUpDialog = true },
                        onConnectClick = { viewModel.onIntent(SyncIntent.OnConnectClick) },
                        onDisconnectClick = { showDisconnectDialog = true },
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(start = 20.dp)) {
                    DescriptionBody()
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                if (state.linkedUuid != null) {
                    LinkedUuidBody(linkedUuid = state.linkedUuid ?: "", lastSyncedAt = state.localLastSyncedAt?.toString() ?: stringResource(Res.string.sync_no_record), lastUpdatedAt = state.serverLastUpdatedAt?.toString() ?: stringResource(Res.string.sync_no_record_or_network))
                } else {
                    UuidBody(uuid = state.uuid, lastSyncedAt = state.localLastSyncedAt?.toString() ?: stringResource(Res.string.sync_no_record), lastUpdatedAt = state.serverLastUpdatedAt?.toString() ?: stringResource(Res.string.sync_no_record_or_network))
                }
                SyncUpBody(
                    isConnected = state.linkedUuid != null,
                    isSyncUpEnabled = state.isSyncUpEnabled,
                    onSyncUpClick = { if (state.isSyncUpEnabled) showSyncUpDialog = true },
                    onConnectClick = { viewModel.onIntent(SyncIntent.OnConnectClick) },
                    onDisconnectClick = { showDisconnectDialog = true },
                )
                DescriptionBody()
            }
        }
    }
    } // BoxWithConstraints
}

@Composable
private fun UuidBody(
    uuid: String,
    lastSyncedAt: String,
    lastUpdatedAt: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.sync_device_id_label),
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = uuid.ifEmpty { stringResource(Res.string.sync_generating) },
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = stringResource(Res.string.sync_device_last_update_label),
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastSyncedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = stringResource(Res.string.sync_server_last_update_label),
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastUpdatedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun LinkedUuidBody(
    linkedUuid: String,
    lastSyncedAt: String,
    lastUpdatedAt: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.sync_linked_id_label),
            style = EbbingTheme.typography.bodySSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = linkedUuid,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = stringResource(Res.string.sync_device_last_update_label),
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastSyncedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Text(
            text = stringResource(Res.string.sync_server_last_update_label),
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        )

        Text(
            text = lastUpdatedAt,
            style = EbbingTheme.typography.bodySR,
            color = EbbingTheme.colors.primaryDefault,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun SyncUpBody(
    isConnected: Boolean,
    isSyncUpEnabled: Boolean,
    onSyncUpClick: () -> Unit,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.sync_section_title),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark2,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSyncUpClick() }
                .padding(vertical = 17.dp),
        ) {
            Text(
                text = stringResource(Res.string.sync_do_sync),
                style = EbbingTheme.typography.headingSSB,
                color = if (isSyncUpEnabled) EbbingTheme.colors.dark1
                else EbbingTheme.colors.dark1.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f),
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = EbbingTheme.colors.dark3,
            )
        }

        Text(
            text = stringResource(Res.string.sync_manual_hint),
            style = EbbingTheme.typography.captionR12,
            color = EbbingTheme.colors.dark2,
        )

        if (isConnected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDisconnectClick() }
                    .padding(vertical = 17.dp),
            ) {
                Text(
                    text = stringResource(Res.string.sync_disconnect_action),
                    style = EbbingTheme.typography.headingSSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.weight(1f),
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = EbbingTheme.colors.dark3,
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onConnectClick() }
                    .padding(vertical = 17.dp),
            ) {
                Text(
                    text = stringResource(Res.string.sync_connect_action),
                    style = EbbingTheme.typography.headingSSB,
                    color = EbbingTheme.colors.dark1,
                    modifier = Modifier.weight(1f),
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = EbbingTheme.colors.dark3,
                )
            }
        }

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun DescriptionBody(
    modifier: Modifier = Modifier,
) {
    val guide1 = stringResource(Res.string.sync_guide_1)
    val guideHighlight1 = stringResource(Res.string.sync_guide_highlight_1)
    val guide2 = stringResource(Res.string.sync_guide_2)
    val guide3 = stringResource(Res.string.sync_guide_3)
    val guideHighlight2 = stringResource(Res.string.sync_guide_highlight_2)
    val guide4 = stringResource(Res.string.sync_guide_4)
    val guide5 = stringResource(Res.string.sync_guide_5)
    Text(
        text = buildAnnotatedString {
            append(guide1)
            append("- ")
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append(guideHighlight1)
            }
            append(guide2)
            append(guide3)
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append(guideHighlight2)
            }
            append(guide4)
            append(guide5)
        },
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark1,
        modifier = modifier,
    )
}

@Composable
private fun SyncUpConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(Res.string.sync_confirm_sync_title),
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            val desc1 = stringResource(Res.string.sync_confirm_sync_desc_1)
            val descHighlight1 = stringResource(Res.string.sync_confirm_sync_desc_highlight_1)
            val desc2 = stringResource(Res.string.sync_confirm_sync_desc_2)
            val descHighlight2 = stringResource(Res.string.sync_confirm_sync_desc_highlight_2)
            val desc3 = stringResource(Res.string.sync_confirm_sync_desc_3)
            Text(
                text = buildAnnotatedString {
                    append(desc1)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append(descHighlight1)
                    }
                    append(desc2)
                    withStyle(style = SpanStyle(color = EbbingTheme.colors.error)) {
                        append(descHighlight2)
                    }
                    append(desc3)
                },
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.sync_back),
                rightButtonText = stringResource(Res.string.sync_sync),
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}

@Composable
private fun DisconnectConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    EbbingDialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            Text(
                text = stringResource(Res.string.sync_confirm_disconnect_title),
                style = EbbingTheme.typography.headingMSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(top = 40.dp),
            )

            Text(
                text = stringResource(Res.string.sync_confirm_disconnect_desc),
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )

            EbbingDialogBottom(
                leftButtonText = stringResource(Res.string.sync_back),
                rightButtonText = stringResource(Res.string.sync_disconnect),
                onLeftButtonClick = onDismiss,
                onRightButtonClick = onConfirm,
            )
        }
    }
}
