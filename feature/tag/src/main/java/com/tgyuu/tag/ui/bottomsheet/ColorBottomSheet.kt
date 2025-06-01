package com.tgyuu.tag.ui.bottomsheet

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tgyuu.common.ui.EbbingVisibleAnimation
import com.tgyuu.common.ui.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingBottomSheetHeader
import com.tgyuu.designsystem.component.EbbingSolidButton

@Composable
internal fun ColorBottomSheet(
    originColor: Int,
    updateColor: (Int) -> Unit,
) {
    var newColor by remember(originColor) { mutableIntStateOf(originColor) }
    val colorOptions: List<Int> = listOf(
        // 빨강 계열 (Red → Light Red)
        0xFFFF0000.toInt(), // Red
        0xFFFF4C4C.toInt(), // Soft Red
        0xFFFF8080.toInt(), // Light Red
        0xFFFF9999.toInt(), // Lighter Red
        0xFFFFB3B3.toInt(), // Very Light Red
        0xFFFFC7C7.toInt(), // Pale Red (조금 더 진하게)

        // 주황 계열 (Orange → Light Orange)
        0xFFFF7F00.toInt(), // Orange
        0xFFFF9933.toInt(), // Soft Orange
        0xFFFFB266.toInt(), // Light Orange
        0xFFFFCC99.toInt(), // Lighter Orange
        0xFFFFD9B3.toInt(), // Very Light Orange
        0xFFFFE5CC.toInt(), // Pale Orange (조금 더 진하게)

        // 노랑 계열 (Yellow → Light Yellow)
        0xFFFFFF00.toInt(), // Yellow
        0xFFFFF000.toInt(), // Vivid Yellow
        0xFFFFF380.toInt(), // Soft Yellow
        0xFFFFF5A3.toInt(), // Light Yellow
        0xFFFFF7C2.toInt(), // Lighter Yellow
        0xFFFFFAE0.toInt(), // Pale Yellow (조금 더 진하게)

        // 초록 계열 (Green → Light Green)
        0xFF008000.toInt(), // Green
        0xFF33A766.toInt(), // Soft Green
        0xFF66C28C.toInt(), // Light Green
        0xFF99DAB3.toInt(), // Lighter Green
        0xFFBFEBD2.toInt(), // Very Light Green
        0xFFE0F8E9.toInt(), // Pale Green (조금 더 진하게)

        // 파랑 계열 (Blue → Light Blue)
        0xFF0000FF.toInt(), // Blue
        0xFF4285F4.toInt(), // Soft Blue
        0xFF6FA8FF.toInt(), // Light Blue
        0xFF99C2FF.toInt(), // Lighter Blue
        0xFFCCE0FF.toInt(), // Very Light Blue
        0xFFE3F0FF.toInt(), // Pale Blue (조금 더 진하게)

        // 보라 계열 (Purple → Light Violet)
        0xFF8A2BE2.toInt(), // Blue Violet
        0xFF9B4DCC.toInt(), // Medium Purple
        0xFFB36EFF.toInt(), // Light Purple
        0xFFD1A3FF.toInt(), // Lighter Purple
        0xFFE5CCFF.toInt(), // Very Light Purple
        0xFFF0E5FF.toInt(), // Pale Violet (조금 더 진하게)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = "색상")

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .heightIn(max = 228.dp),
        ) {
            items(colorOptions) { colorValue ->
                val baseColor = Color(colorValue)

                val displayColor by animateColorAsState(
                    targetValue = if (newColor == colorValue) lerp(baseColor, Color.Black, 0.2f)
                    else baseColor
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(displayColor)
                            .clickable { newColor = colorValue }
                    )

                    EbbingVisibleAnimation(newColor == colorValue) {
                        Image(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        EbbingSolidButton(
            label = "적용하기",
            onClick = { updateColor(newColor) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}
