package com.tgyuu.home.graph.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tgyuu.common.util.isLandscapeWindow

/**
 * 태블릿 일정 등록/수정 화면 배치.
 * - 가로: 폼(+하단 버튼) | 보조 영역(학습 일정)을 같은 폭으로 나란히, 좌우 48dp·사이 80dp
 * - 세로: 폼 아래에 보조 영역을 이어 붙이고 버튼은 하단 고정, 좌우 60dp
 */
@Composable
internal fun TabletFormLayout(
    form: @Composable ColumnScope.() -> Unit,
    button: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    side: (@Composable ColumnScope.() -> Unit)? = null,
) {
    val buttonModifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 16.dp)

    if (isLandscapeWindow()) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(vertical = 20.dp),
                    content = form,
                )

                button(buttonModifier)
            }

            Spacer(modifier = Modifier.width(80.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 20.dp),
            ) {
                side?.invoke(this)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 60.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(vertical = 20.dp),
            ) {
                form()
                side?.invoke(this)
            }

            button(buttonModifier)
        }
    }
}
