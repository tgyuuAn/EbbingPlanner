package com.tgyuu.sync.graph.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.graph.connect.contract.ConnectIntent
import com.tgyuu.sync.graph.connect.contract.ConnectState
import com.tgyuu.sync.graph.connect.ui.dialog.ConfirmConnectDialog

@Composable
internal fun ConnectRoute(viewModel: ConnectViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.getMyConnectInfo()
    }

    ConnectScreen(
        state = state,
        onBackClick = { viewModel.onIntent(ConnectIntent.OnBackClick) },
        onMyCodeChange = { viewModel.onIntent(ConnectIntent.OnMyCodeChange(it)) },
        onAnotherCodeChange = { viewModel.onIntent(ConnectIntent.OnAnotherCodeChange(it)) },
        onClickConnectAnother = { viewModel.onIntent(ConnectIntent.OnClickConnectAnother) },
        onClickGenerateCode = { viewModel.onIntent(ConnectIntent.OnClickGenerateCode) },
    )
}

@Composable
internal fun ConnectScreen(
    state: ConnectState,
    onBackClick: () -> Unit,
    onMyCodeChange: (String) -> Unit,
    onAnotherCodeChange: (String) -> Unit,
    onClickGenerateCode: () -> Unit,
    onClickConnectAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isShowConnectDialog by remember { mutableStateOf(false) }

    if (isShowConnectDialog) {
        ConfirmConnectDialog(
            onDismissRequest = { isShowConnectDialog = false },
            onAcceptClick = {
                onClickConnectAnother()
                isShowConnectDialog = false
            },
        )
    }

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneConnectScreen(
            state = state,
            onBackClick = onBackClick,
            onMyCodeChange = onMyCodeChange,
            onAnotherCodeChange = onAnotherCodeChange,
            onClickGenerateCode = onClickGenerateCode,
            onClickConnectAnother = { isShowConnectDialog = true },
            modifier = modifier,
        )
    } else {
        TabletConnectScreen(
            state = state,
            onBackClick = onBackClick,
            onMyCodeChange = onMyCodeChange,
            onAnotherCodeChange = onAnotherCodeChange,
            onClickGenerateCode = onClickGenerateCode,
            onClickConnectAnother = { isShowConnectDialog = true },
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneConnectScreen(
    state: ConnectState,
    onBackClick: () -> Unit,
    onMyCodeChange: (String) -> Unit,
    onAnotherCodeChange: (String) -> Unit,
    onClickGenerateCode: () -> Unit,
    onClickConnectAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "다른 기기와 연동",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        LinkBody(
            myCode = state.myCode,
            anotherCode = state.anotherCode,
            remainingTimeInSec = state.formattedRemainingTimeInSec,
            isGenerateButtonEnabled = state.isGenerateButtonEnabled,
            isConnectButtonEnabled = state.isConnectButtonEnabled,
            onMyCodeChange = onMyCodeChange,
            onAnotherCodeChange = onAnotherCodeChange,
            onClickGenerateCode = onClickGenerateCode,
            onClickConnectAnother = onClickConnectAnother,
        )

        DescriptionBody()
    }
}

@Composable
private fun TabletConnectScreen(
    state: ConnectState,
    onBackClick: () -> Unit,
    onMyCodeChange: (String) -> Unit,
    onAnotherCodeChange: (String) -> Unit,
    onClickGenerateCode: () -> Unit,
    onClickConnectAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
    ) {
        EbbingSubTopBar(
            title = "다른 기기와 연동",
            onNavigationClick = onBackClick,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                LinkBody(
                    myCode = state.myCode,
                    anotherCode = state.anotherCode,
                    remainingTimeInSec = state.formattedRemainingTimeInSec,
                    isGenerateButtonEnabled = state.isGenerateButtonEnabled,
                    isConnectButtonEnabled = state.isConnectButtonEnabled,
                    onMyCodeChange = onMyCodeChange,
                    onAnotherCodeChange = onAnotherCodeChange,
                    onClickGenerateCode = onClickGenerateCode,
                    onClickConnectAnother = onClickConnectAnother,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                DescriptionBody()
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
    var lastDoneTime by remember { mutableLongStateOf(0L) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        Text(
            text = "내 기기 데이터로 연동시키기",
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
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastDoneTime >= 1000L) {
                            keyboardController?.hide()
                            if (myCode.isNotEmpty() && isGenerateButtonEnabled) {
                                onClickGenerateCode()
                            }
                            lastDoneTime = currentTime
                        }
                    }
                ),
                textStyle = EbbingTheme.typography.bodyMM.copy(
                    color = if (isGenerateButtonEnabled) EbbingTheme.colors.black
                    else EbbingTheme.colors.primaryDefault
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
                label = "생성",
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastDoneTime >= 1000L) {
                        keyboardController?.hide()
                        onClickGenerateCode()
                        lastDoneTime = currentTime
                    }
                },
                enabled = myCode.isNotEmpty() && isGenerateButtonEnabled,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "내 데이터로 연동할 수 있는 코드를 생성하세요.",
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "다른 기기 데이터로 연동하기",
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
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastDoneTime >= 1000L) {
                            keyboardController?.hide()
                            if (anotherCode.isNotEmpty() && isConnectButtonEnabled) {
                                onClickConnectAnother()
                            }
                            lastDoneTime = currentTime
                        }
                    }
                ),
                textStyle = EbbingTheme.typography.bodyMM.copy(
                    color = if (isConnectButtonEnabled) EbbingTheme.colors.black
                    else EbbingTheme.colors.primaryDefault
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
                label = "연결",
                onClick = {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastDoneTime >= 1000L) {
                        keyboardController?.hide()
                        onClickConnectAnother()
                        lastDoneTime = currentTime
                    }
                },
                enabled = anotherCode.isNotEmpty() && isConnectButtonEnabled,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = "사용할 데이터를 가진 기기의 연동 코드를 입력하세요.",
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.dark3,
        )

        HorizontalDivider(
            color = EbbingTheme.colors.light2,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun DescriptionBody() {
    Text(
        text = buildAnnotatedString {
            append("1. 데이터를 보존할 기기에서 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append("연동 코드를 생성")
            }
            append("하세요.\n")
            append("2. 생성된 코드는 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append("10분간 유효")
            }
            append("합니다.\n")
            append("3. 다른 기기에서 해당 코드를 입력해 연동을 완료하세요.\n")
            append("4. 코드를 입력하는 기기의 ")
            withStyle(SpanStyle(color = EbbingTheme.colors.error)) {
                append("기존 데이터는 덮어씌워지므로, ")
            }
            append("신중히 선택해 주세요.")
        },
        textAlign = TextAlign.Start,
        style = EbbingTheme.typography.bodyMM,
        color = EbbingTheme.colors.dark1,
    )
}

