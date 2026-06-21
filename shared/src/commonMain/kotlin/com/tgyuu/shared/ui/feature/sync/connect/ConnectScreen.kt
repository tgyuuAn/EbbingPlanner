package com.tgyuu.shared.ui.feature.sync.connect

import com.tgyuu.shared.designsystem.foundation.LayoutConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import com.tgyuu.shared.designsystem.component.EbbingDialog
import com.tgyuu.shared.designsystem.component.EbbingDialogBottom
import com.tgyuu.shared.designsystem.component.EbbingDialogDefaultTop
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.component.EbbingSubTopBar
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.connect_connect
import ebbingplanner.shared.generated.resources.connect_dialog_confirm
import ebbingplanner.shared.generated.resources.connect_dialog_desc_1
import ebbingplanner.shared.generated.resources.connect_dialog_desc_2
import ebbingplanner.shared.generated.resources.connect_dialog_desc_3
import ebbingplanner.shared.generated.resources.connect_dialog_desc_highlight_1
import ebbingplanner.shared.generated.resources.connect_dialog_desc_highlight_2
import ebbingplanner.shared.generated.resources.connect_dialog_title
import ebbingplanner.shared.generated.resources.connect_download_desc
import ebbingplanner.shared.generated.resources.connect_download_title
import ebbingplanner.shared.generated.resources.connect_generate
import ebbingplanner.shared.generated.resources.connect_guide_1
import ebbingplanner.shared.generated.resources.connect_guide_2
import ebbingplanner.shared.generated.resources.connect_guide_3
import ebbingplanner.shared.generated.resources.connect_guide_4
import ebbingplanner.shared.generated.resources.connect_guide_highlight_1
import ebbingplanner.shared.generated.resources.connect_guide_highlight_2
import ebbingplanner.shared.generated.resources.connect_guide_highlight_3
import ebbingplanner.shared.generated.resources.connect_title
import ebbingplanner.shared.generated.resources.connect_upload_desc
import ebbingplanner.shared.generated.resources.connect_upload_title
import ebbingplanner.shared.generated.resources.sync_back
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConnectScreen(
    viewModel: ConnectViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    var isShowConnectDialog by remember { mutableStateOf(false) }

    if (isShowConnectDialog) {
        EbbingDialog(
            dialogTop = {
                val descPart1 = stringResource(Res.string.connect_dialog_desc_1)
                val descHighlight1 = stringResource(Res.string.connect_dialog_desc_highlight_1)
                val descPart2 = stringResource(Res.string.connect_dialog_desc_2)
                val descHighlight2 = stringResource(Res.string.connect_dialog_desc_highlight_2)
                val descPart3 = stringResource(Res.string.connect_dialog_desc_3)
                EbbingDialogDefaultTop(
                    title = stringResource(Res.string.connect_dialog_title),
                    subText = buildAnnotatedString {
                        append(descPart1)
                        withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                            append(descHighlight1)
                        }
                        append(descPart2)
                        withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                            append(descHighlight2)
                        }
                        append(descPart3)
                    },
                )
            },
            dialogBottom = {
                EbbingDialogBottom(
                    leftButtonText = stringResource(Res.string.sync_back),
                    rightButtonText = stringResource(Res.string.connect_dialog_confirm),
                    onLeftButtonClick = { isShowConnectDialog = false },
                    onRightButtonClick = {
                        isShowConnectDialog = false
                        viewModel.onIntent(ConnectIntent.OnClickConnectAnother)
                    },
                )
            },
            onDismissRequest = { isShowConnectDialog = false },
        )
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        val isWide = maxWidth > LayoutConstants.TABLET_BREAKPOINT
        Column(modifier = Modifier.fillMaxSize()) {
            EbbingSubTopBar(
                title = stringResource(Res.string.connect_title),
                onNavigationClick = { viewModel.onIntent(ConnectIntent.OnBackClick) },
                modifier = Modifier.padding(bottom = 20.dp),
            )

            if (isWide) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState),
                    ) {
                        LinkBody(
                            myCode = state.myCode,
                            anotherCode = state.anotherCode,
                            remainingTimeInSec = state.formattedRemainingTimeInSec,
                            isGenerateButtonEnabled = state.isGenerateButtonEnabled,
                            isConnectButtonEnabled = state.isConnectButtonEnabled,
                            onMyCodeChange = { viewModel.onIntent(ConnectIntent.OnMyCodeChange(it)) },
                            onAnotherCodeChange = { viewModel.onIntent(ConnectIntent.OnAnotherCodeChange(it)) },
                            onClickGenerateCode = { viewModel.onIntent(ConnectIntent.OnClickGenerateCode) },
                            onClickConnectAnother = { isShowConnectDialog = true },
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 20.dp),
                    ) {
                        DescriptionBody()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .fillMaxWidth(),
                ) {
                    LinkBody(
                        myCode = state.myCode,
                        anotherCode = state.anotherCode,
                        remainingTimeInSec = state.formattedRemainingTimeInSec,
                        isGenerateButtonEnabled = state.isGenerateButtonEnabled,
                        isConnectButtonEnabled = state.isConnectButtonEnabled,
                        onMyCodeChange = { viewModel.onIntent(ConnectIntent.OnMyCodeChange(it)) },
                        onAnotherCodeChange = { viewModel.onIntent(ConnectIntent.OnAnotherCodeChange(it)) },
                        onClickGenerateCode = { viewModel.onIntent(ConnectIntent.OnClickGenerateCode) },
                        onClickConnectAnother = { isShowConnectDialog = true },
                    )
                    DescriptionBody()
                }
            }
        }
    }
}

@Composable
private fun LinkBody(
    myCode: String,
    anotherCode: String,
    remainingTimeInSec: String,
    isGenerateButtonEnabled: Boolean,
    isConnectButtonEnabled: Boolean,
    onMyCodeChange: (String) -> Unit,
    onAnotherCodeChange: (String) -> Unit,
    onClickGenerateCode: () -> Unit,
    onClickConnectAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.connect_upload_title),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            BasicTextField(
                value = myCode,
                onValueChange = onMyCodeChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                readOnly = !isGenerateButtonEnabled,
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (myCode.isNotEmpty() && isGenerateButtonEnabled) {
                            onClickGenerateCode()
                        }
                    },
                ),
                textStyle = EbbingTheme.typography.bodyMM.copy(
                    color = if (isGenerateButtonEnabled) EbbingTheme.colors.black
                    else EbbingTheme.colors.primaryDefault,
                ),
                decorationBox = { innerTextField ->
                    Box {
                        innerTextField()
                        if (!isGenerateButtonEnabled) {
                            Text(
                                text = remainingTimeInSec,
                                style = EbbingTheme.typography.bodySM,
                                color = EbbingTheme.colors.primaryDefault,
                                modifier = Modifier.align(Alignment.CenterEnd),
                            )
                        }
                    }
                },
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .weight(1f),
            )

            EbbingSolidButton(
                label = stringResource(Res.string.connect_generate),
                onClick = {
                    keyboardController?.hide()
                    onClickGenerateCode()
                },
                enabled = myCode.isNotEmpty() && isGenerateButtonEnabled,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Text(
            text = stringResource(Res.string.connect_upload_desc),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp),
        )

        Text(
            text = stringResource(Res.string.connect_download_title),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.black,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            BasicTextField(
                value = anotherCode,
                onValueChange = onAnotherCodeChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                readOnly = !isConnectButtonEnabled,
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        if (anotherCode.isNotEmpty() && isConnectButtonEnabled) {
                            onClickConnectAnother()
                        }
                    },
                ),
                textStyle = EbbingTheme.typography.bodyMM.copy(
                    color = if (isConnectButtonEnabled) EbbingTheme.colors.black
                    else EbbingTheme.colors.primaryDefault,
                ),
                decorationBox = { innerTextField -> innerTextField() },
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(EbbingTheme.colors.light3)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .weight(1f),
            )

            EbbingSolidButton(
                label = stringResource(Res.string.connect_connect),
                onClick = {
                    keyboardController?.hide()
                    if (anotherCode.isNotEmpty() && isConnectButtonEnabled) {
                        onClickConnectAnother()
                    }
                },
                enabled = anotherCode.isNotEmpty() && isConnectButtonEnabled,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        Text(
            text = stringResource(Res.string.connect_download_desc),
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp),
        )
    }
}

@Composable
private fun DescriptionBody(
    modifier: Modifier = Modifier,
) {
    val guide1 = stringResource(Res.string.connect_guide_1)
    val guideHighlight1 = stringResource(Res.string.connect_guide_highlight_1)
    val guide2 = stringResource(Res.string.connect_guide_2)
    val guideHighlight2 = stringResource(Res.string.connect_guide_highlight_2)
    val guide3 = stringResource(Res.string.connect_guide_3)
    val guideHighlight3 = stringResource(Res.string.connect_guide_highlight_3)
    val guide4 = stringResource(Res.string.connect_guide_4)
    Text(
        text = buildAnnotatedString {
            append(guide1)
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append(guideHighlight1)
            }
            append(guide2)
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append(guideHighlight2)
            }
            append(guide3)
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append(guideHighlight3)
            }
            append(guide4)
        },
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark1,
        modifier = modifier,
    )
}
