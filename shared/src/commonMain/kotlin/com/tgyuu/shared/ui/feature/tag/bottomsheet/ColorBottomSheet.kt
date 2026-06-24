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

// 6 color families with 6 shades each (dark to light)
val TAG_COLORS = listOf(
    // Red
    0xFFB71C1C.toInt(), 0xFFD32F2F.toInt(), 0xFFE53935.toInt(),
    0xFFEF5350.toInt(), 0xFFEF9A9A.toInt(), 0xFFFFCDD2.toInt(),
    // Orange
    0xFFE65100.toInt(), 0xFFF57C00.toInt(), 0xFFFF9800.toInt(),
    0xFFFFB74D.toInt(), 0xFFFFCC80.toInt(), 0xFFFFE0B2.toInt(),
    // Yellow
    0xFFF9A825.toInt(), 0xFFFBC02D.toInt(), 0xFFFFEB3B.toInt(),
    0xFFFFF176.toInt(), 0xFFFFF59D.toInt(), 0xFFFFF9C4.toInt(),
    // Green
    0xFF1B5E20.toInt(), 0xFF388E3C.toInt(), 0xFF4CAF50.toInt(),
    0xFF81C784.toInt(), 0xFFA5D6A7.toInt(), 0xFFC8E6C9.toInt(),
    // Blue
    0xFF0D47A1.toInt(), 0xFF1976D2.toInt(), 0xFF2196F3.toInt(),
    0xFF64B5F6.toInt(), 0xFF90CAF9.toInt(), 0xFFBBDEFB.toInt(),
    // Purple
    0xFF4A148C.toInt(), 0xFF7B1FA2.toInt(), 0xFF9C27B0.toInt(),
    0xFFBA68C8.toInt(), 0xFFCE93D8.toInt(), 0xFFE1BEE7.toInt(),
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
