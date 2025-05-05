package com.tgyuu.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tgyuu.designsystem.BasePreview
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.component.EbbingSolidButton
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.onboarding.contract.OnboardingIntent
import com.tgyuu.onboarding.contract.OnboardingState
import kotlinx.coroutines.launch

@Composable
internal fun OnboardingRoute(
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingScreen(
        state = state,
        onStartClick = { viewModel.onIntent(OnboardingIntent.OnStartClick) },
    )
}

@Composable
private fun OnboardingScreen(
    state: OnboardingState,
    onStartClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 4 },
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 40.dp)
        ) { page ->
            Column(modifier = Modifier.fillMaxSize()) {
                when (page) {
                    0 -> OnboardingPageContent(
                        imageRes = R.drawable.ic_onboarding_1,
                        title = stringResource(R.string.onboarding_1_title),
                        description = stringResource(R.string.onboarding_1_description),
                    )

                    1 -> OnboardingPageContent(
                        imageRes = R.drawable.ic_onboarding_2,
                        title = stringResource(R.string.onboarding_2_title),
                        description = stringResource(R.string.onboarding_2_description),
                    )

                    2 -> OnboardingPageContent(
                        imageRes = R.drawable.ic_onboarding_3,
                        title = stringResource(R.string.onboarding_3_title),
                        description = stringResource(R.string.onboarding_3_description),
                    )

                    3 -> OnboardingPageContent(
                        imageRes = R.drawable.ic_onboarding_4,
                        title = stringResource(R.string.onboarding_4_title),
                        description = stringResource(R.string.onboarding_4_description),
                    )
                }
            }
        }

        OnboardingIndicator(
            total = 4,
            current = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 30.dp),
        )

        EbbingSolidButton(
            label = if (pagerState.currentPage == 3) {
                stringResource(R.string.start)
            } else {
                stringResource(R.string.next)
            },
            onClick = {
                if (pagerState.currentPage == 3) {
                    onStartClick()
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            },
            modifier = Modifier
                .padding(bottom = 10.dp, top = 12.dp)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun OnboardingPageContent(
    imageRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.primaryDefault),
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(vertical = 66.dp)
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = title,
                textAlign = TextAlign.Start,
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Text(
                text = description,
                textAlign = TextAlign.Start,
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark3,
            )
        }
    }
}

@Composable
private fun OnboardingIndicator(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        (0 until total).forEachIndexed { index, _ ->
            if (index == current) {
                Spacer(
                    modifier = Modifier
                        .size(width = 20.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(EbbingTheme.colors.dark2)
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EbbingTheme.colors.light1)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewOnboardingScreen() {
    BasePreview {
        OnboardingScreen(
            state = OnboardingState(),
            onStartClick = {},
        )
    }
}
