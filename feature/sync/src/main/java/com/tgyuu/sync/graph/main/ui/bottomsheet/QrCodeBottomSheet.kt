package com.tgyuu.sync.graph.main.ui.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.EbbingTheme
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter

@Composable
fun QrCodeBottomSheet(
    qrContent: String,
    formattedRemainingTime: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = "연동 QR 코드")

        val painter = rememberQrCodePainter(data = qrContent) {
            shapes {
                ball = QrBallShape.roundCorners(0.25f)
                darkPixel = QrPixelShape.roundCorners()
                frame = QrFrameShape.roundCorners(0.25f)
            }
        }

        Image(
            painter = painter,
            contentDescription = "연동 QR 코드",
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .padding(vertical = 24.dp),
        )

        Text(
            text = "남은 시간: $formattedRemainingTime",
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.primaryNormal,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        Text(
            text = "다른 기기에서 이 QR 코드를 스캔해주세요.",
            style = EbbingTheme.typography.body14M,
            color = EbbingTheme.colors.textDisabled,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}
