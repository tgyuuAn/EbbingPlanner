package com.tgyuu.home.graph.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tgyuu.designsystem.component.EbbingSelectDropdown
import com.tgyuu.designsystem.component.EbbingTextInputDropDown

/** 드롭다운 항목 content. 인자로 받은 dismiss를 부르면 드롭다운이 닫혀요 */
internal typealias SelectDropdownContent = @Composable ColumnScope.(dismiss: () -> Unit) -> Unit

/**
 * 태그·반복 주기 선택 필드.
 * [dropdown]이 있으면(태블릿) 바텀시트 대신 필드 바로 아래에 같은 폭의 드롭다운을 띄워요.
 */
@Composable
internal fun SelectDropdownField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Int? = null,
    dropdown: SelectDropdownContent? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var fieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { fieldWidth = with(density) { it.width.toDp() } },
        ) {
            EbbingTextInputDropDown(
                value = value,
                color = color,
                expanded = expanded,
                onDropDownClick = { if (dropdown == null) onClick() else expanded = true },
                modifier = Modifier.fillMaxWidth(),
            )

            if (dropdown != null && expanded) {
                EbbingSelectDropdown(
                    width = fieldWidth,
                    onDismissRequest = { expanded = false },
                ) {
                    dropdown { expanded = false }
                }
            }
        }
    }
}
