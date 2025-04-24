package com.tgyuu.home.graph.addtag.ui.bottomsheet

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
import androidx.compose.runtime.mutableStateOf
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
    var newColor by remember(originColor) { mutableStateOf(originColor) }
    val colorOptions: List<Int> = listOf(
        0xFFFF6961.toInt(), // Pastel Red
        0xFFF88379.toInt(), // Pastel Coral
        0xFFFFB347.toInt(), // Pastel Orange
        0xFFFFDAB9.toInt(), // Pastel Peach
        0xFFFFD1DC.toInt(), // Pastel Pink
        0xFFFFCCE5.toInt(), // Pastel Rose
        0xFFF4C2C2.toInt(), // Pastel Blush
        0xFFFFB6C1.toInt(), // Pastel Baby Pink
        0xFFFDFD96.toInt(), // Pastel Yellow
        0xFFFFFACD.toInt(), // Pastel Lemon
        0xFFFAF0E6.toInt(), // Pastel Sand (Linen)
        0xFF77DD77.toInt(), // Pastel Green
        0xFFAAF0D1.toInt(), // Pastel Mint
        0xFF9FE2BF.toInt(), // Pastel Seafoam
        0xFFC2DDB2.toInt(), // Pastel Moss
        0xFF99E1D9.toInt(), // Pastel Teal
        0xFFB2FFFF.toInt(), // Pastel Aqua
        0xFFB0E0E6.toInt(), // Pastel Sky Blue
        0xFFAEC6CF.toInt(), // Pastel Blue
        0xFFCCCCFF.toInt(), // Pastel Periwinkle
        0xFFE3E4FA.toInt(), // Pastel Lavender
        0xFFC8A2C8.toInt(), // Pastel Lilac
        0xFFE0B0FF.toInt(), // Pastel Mauve
        0xFFC39BD3.toInt()  // Pastel Purple
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
                .heightIn(max = 400.dp)
                .padding(vertical = 20.dp),
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
