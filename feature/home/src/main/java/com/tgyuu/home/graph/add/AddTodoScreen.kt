package com.tgyuu.home.graph.add

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.designsystem.component.BasePreview
import com.tgyuu.designsystem.foundation.EbbingTheme
import java.time.LocalDate

@Composable
internal fun AddTodoRoute(
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddTodoScreen(
        selectedDate = state.selectedDate,
        onSelectedDateChangeClick = {},
    )
}

@Composable
private fun AddTodoScreen(
    selectedDate: LocalDate,
    onSelectedDateChangeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                    append("${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일")
                }
                append(" 부터\n시작하는 일정을 만듭니다.")
            },
            style = EbbingTheme.typography.headingLSB,
            color = EbbingTheme.colors.black,
            modifier = Modifier.clickable { onSelectedDateChangeClick() },
        )


    }
}

@Preview
@Composable
private fun Preview1() {
    BasePreview {
        AddTodoScreen(
            selectedDate = LocalDate.now(),
            onSelectedDateChangeClick = {},
        )
    }
}
