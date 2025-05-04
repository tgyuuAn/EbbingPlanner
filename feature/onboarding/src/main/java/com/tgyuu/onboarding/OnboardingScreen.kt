package com.tgyuu.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.onboarding.contract.OnboardingState

@Composable
internal fun OnboardingRoute(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(state = state)
}

@Composable
private fun OnboardingScreen(
    state: OnboardingState,
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}

@Preview
@Composable
private fun PreviewOnboardingScreen() {
    BasePreview {
        OnboardingScreen(
            state = OnboardingState(),
        )
    }
}
