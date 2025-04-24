package com.tgyuu.designsystem.component

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.foundation.EbbingTheme
import kotlinx.coroutines.delay

@Composable
fun EbbingSnackBar(
    snackBarData: SnackbarData,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 60.dp, start = 20.dp, end = 20.dp)
            .wrapContentSize()
            .clip(RoundedCornerShape(12.dp))
            .background(EbbingTheme.colors.dark3)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = snackBarData.visuals.message,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = EbbingTheme.typography.bodySM,
            color = EbbingTheme.colors.white,
        )
    }
}

@Composable
fun EbbingSnackBarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarData) -> Unit = { Snackbar(it) }
) {
    val currentSnackbarData = hostState.currentSnackbarData
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            delay(2000L)
            currentSnackbarData.dismiss()
        }
    }

    Crossfade(
        targetState = hostState.currentSnackbarData,
        modifier = modifier,
        label = "",
        content = { current -> if (current != null) snackbar(current) },
    )
}

@Preview
@Composable
private fun PreviewSnackBar() {
    BasePreview {
        EbbingSnackBar(
            snackBarData = object : SnackbarData {
                override val visuals = object : SnackbarVisuals {
                    override val actionLabel = null
                    override val duration = SnackbarDuration.Short
                    override val message = "텍스트만 있는 스낵바입니다"
                    override val withDismissAction = false
                }

                override fun dismiss() {}
                override fun performAction() {}
            },
        )
    }
}
