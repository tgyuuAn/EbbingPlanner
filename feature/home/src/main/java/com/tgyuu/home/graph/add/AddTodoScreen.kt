package com.tgyuu.home.graph.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.tgyuu.designsystem.component.BasePreview

@Composable
internal fun AddTodoRoute(
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    AddTodoScreen()
}

@Composable
private fun AddTodoScreen(modifier: Modifier = Modifier) {

}

@Preview
@Composable
private fun Preview1() {
    BasePreview {
        AddTodoScreen()
    }
}
