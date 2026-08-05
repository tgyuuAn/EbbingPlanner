package com.tgyuu.shared.ui.feature.tag.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.bottomsheet.EbbingBottomSheetHeader
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.tag_manage_title
import ebbingplanner.shared.generated.resources.tag_add_button
import ebbingplanner.shared.generated.resources.tag_empty_message
import ebbingplanner.shared.generated.resources.tag_delete
import ebbingplanner.shared.generated.resources.tag_edit
import ebbingplanner.shared.generated.resources.tag_back
import ebbingplanner.shared.generated.resources.tag_delete_confirm_prefix
import ebbingplanner.shared.generated.resources.tag_delete_confirm_highlight
import ebbingplanner.shared.generated.resources.tag_delete_confirm_suffix
import ebbingplanner.shared.generated.resources.tag_delete_confirm_subtext
import ebbingplanner.shared.generated.resources.tag_add_title
import ebbingplanner.shared.generated.resources.tag_edit_title
import ebbingplanner.shared.generated.resources.tag_save
import ebbingplanner.shared.generated.resources.tag_add_headline
import ebbingplanner.shared.generated.resources.tag_edit_headline
import ebbingplanner.shared.generated.resources.tag_name_label
import ebbingplanner.shared.generated.resources.tag_name_hint
import ebbingplanner.shared.generated.resources.tag_color
import ebbingplanner.shared.generated.resources.tag_color_select_title
import ebbingplanner.shared.generated.resources.tag_apply
import ebbingplanner.shared.generated.resources.common_clear
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_check

// Android core/designsystem ColorOptions와 동일한 6계열 × 6음영 (동기화 색상값 일치)
val TAG_COLORS = listOf(
    // Red
    0xFFFF0000.toInt(), 0xFFFF4C4C.toInt(), 0xFFFF8080.toInt(),
    0xFFFF9999.toInt(), 0xFFFFB3B3.toInt(), 0xFFFFC7C7.toInt(),
    // Orange
    0xFFFF7F00.toInt(), 0xFFFF9933.toInt(), 0xFFFFB266.toInt(),
    0xFFFFCC99.toInt(), 0xFFFFD9B3.toInt(), 0xFFFFE5CC.toInt(),
    // Yellow
    0xFFFFFF00.toInt(), 0xFFFFF000.toInt(), 0xFFFFF380.toInt(),
    0xFFFFF5A3.toInt(), 0xFFFFF7C2.toInt(), 0xFFFFFAE0.toInt(),
    // Green
    0xFF008000.toInt(), 0xFF33A766.toInt(), 0xFF66C28C.toInt(),
    0xFF99DAB3.toInt(), 0xFFBFEBD2.toInt(), 0xFFE0F8E9.toInt(),
    // Blue
    0xFF0000FF.toInt(), 0xFF4285F4.toInt(), 0xFF6FA8FF.toInt(),
    0xFF99C2FF.toInt(), 0xFFCCE0FF.toInt(), 0xFFE3F0FF.toInt(),
    // Purple
    0xFF8A2BE2.toInt(), 0xFF9B4DCC.toInt(), 0xFFB36EFF.toInt(),
    0xFFD1A3FF.toInt(), 0xFFE5CCFF.toInt(), 0xFFF0E5FF.toInt(),
)

@Composable
fun ColorBottomSheet(
    currentColor: Int,
    onColorSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedColor by remember { mutableStateOf(currentColor) }

    Column(
        modifier = modifier.padding(bottom = 32.dp),
    ) {
        EbbingBottomSheetHeader(
            title = stringResource(Res.string.tag_color_select_title),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            items(TAG_COLORS) { color ->
                ColorItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { selectedColor = color },
                )
            }
        }

        EbbingSolidButton(
            label = stringResource(Res.string.tag_apply),
            onClick = {
                onColorSelect(selectedColor)
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
    }
}

@Composable
private fun ColorItem(
    color: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(color))
            .then(
                if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = EbbingTheme.colors.black,
                    shape = CircleShape
                ) else Modifier
            )
            .clickable { onClick() },
    ) {
        if (isSelected) {
            Icon(
                painter = painterResource(Res.drawable.ic_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
