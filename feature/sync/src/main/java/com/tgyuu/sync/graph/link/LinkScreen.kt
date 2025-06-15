package com.tgyuu.sync.graph.link

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.EbbingSubTopBar
import com.tgyuu.designsystem.component.EbbingTextInputDefault
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.sync.graph.link.contract.LinkIntent
import com.tgyuu.sync.graph.link.contract.LinkState

@Composable
internal fun LinkRoute(
    viewModel: LinkViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LinkScreen(
        state = state,
        onBackClick = { viewModel.onIntent(LinkIntent.OnBackClick) },
        onLinkClick = { viewModel.onIntent(LinkIntent.OnLinkClick) },
    )
}

@Composable
internal fun LinkScreen(
    state: LinkState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
        PhoneLinkScreen(
            state = state,
            onBackClick = onBackClick,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
    } else {
        TabletLinkScreen(
            state = state,
            onBackClick = onBackClick,
            onLinkClick = onLinkClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun PhoneLinkScreen(
    state: LinkState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
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
            code = state.code,
            isGenerateButtonEnabled = state.isGenerateButtonEnabled,
            onCodeChange = {},
            onClickCodeGenerate = {},
        )

        DescriptionBody()
    }
}

@Composable
private fun TabletLinkScreen(
    state: LinkState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
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
            code = state.code,
            isGenerateButtonEnabled = state.isGenerateButtonEnabled,
            onCodeChange = {},
            onClickCodeGenerate = {},
        )
    }
}

@Composable
private fun LinkBody(
    code: String,
    isGenerateButtonEnabled: Boolean,
    onCodeChange: (String) -> Unit,
    onClickCodeGenerate: () -> Unit,
) {
    Text(
        text = "내 기기 데이터로 연동시키기",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        EbbingTextInputDefault(
            value = code,
            hint = "내 연동 코드를 생성하세요.",
            keyboardType = KeyboardType.Text,
            onValueChange = onCodeChange,
            limit = 100,
            rightComponent = {
                if (code.isNotEmpty()) {
                    Image(
                        painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_delete_circle),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clickable { onCodeChange("") },
                    )
                }
            },
            modifier = Modifier.weight(1f),
        )

        EbbingSolidButton(
            label = "생성",
            onClick = onClickCodeGenerate,
            enabled = isGenerateButtonEnabled,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    Text(
        text = "다른 기기 데이터로 연동하기",
        style = EbbingTheme.typography.bodySM,
        color = EbbingTheme.colors.dark2,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    ) {
        EbbingTextInputDefault(
            value = code,
            hint = "다른 연동 코드를 입력하세요.",
            keyboardType = KeyboardType.Text,
            onValueChange = onCodeChange,
            limit = 100,
            rightComponent = {
                if (code.isNotEmpty()) {
                    Image(
                        painter = painterResource(com.tgyuu.designsystem.R.drawable.ic_delete_circle),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(20.dp)
                            .clickable { onCodeChange("") },
                    )
                }
            },
            modifier = Modifier.weight(1f),
        )

        EbbingSolidButton(
            label = "입력",
            onClick = onClickCodeGenerate,
            enabled = isGenerateButtonEnabled,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    HorizontalDivider(
        color = EbbingTheme.colors.light2,
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
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
        color = EbbingTheme.colors.dark3,
    )
}
