package com.tgyuu.tag.ui.bottomsheet

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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.tgyuu.common.util.EbbingVisibleAnimation
import com.tgyuu.common.util.clickable
import com.tgyuu.common.util.ebbingAnimateColorAsState
import com.tgyuu.common.util.verticalScrollbar
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.designsystem.foundation.ColorOptions
import com.tgyuu.designsystem.foundation.EbbingTheme

@Composable
internal fun ColorBottomSheet(
    originColor: Int,
    updateColor: (Int) -> Unit,
) {
    var newColor by remember(originColor) { mutableIntStateOf(originColor) }
    val listState = rememberLazyGridState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        EbbingBottomSheetHeader(title = "색상")

        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
                .heightIn(max = 228.dp)
                .verticalScrollbar(
                    state = listState,
                    color = EbbingTheme.colors.fillDisabled,
                ),
        ) {
            items(ColorOptions) { colorValue ->
                val baseColor = Color(colorValue)

                val displayColor = ebbingAnimateColorAsState(
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
