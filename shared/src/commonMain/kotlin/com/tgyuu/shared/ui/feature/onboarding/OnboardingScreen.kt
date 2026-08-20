package com.tgyuu.shared.ui.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import kotlinx.coroutines.launch
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.onboarding_next
import ebbingplanner.shared.generated.resources.onboarding_page1_desc
import ebbingplanner.shared.generated.resources.onboarding_page1_title
import ebbingplanner.shared.generated.resources.onboarding_page2_desc
import ebbingplanner.shared.generated.resources.onboarding_page2_title
import ebbingplanner.shared.generated.resources.onboarding_page3_desc
import ebbingplanner.shared.generated.resources.onboarding_page3_title
import ebbingplanner.shared.generated.resources.onboarding_page4_desc
import ebbingplanner.shared.generated.resources.onboarding_page4_title
import ebbingplanner.shared.generated.resources.onboarding_start
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import ebbingplanner.shared.generated.resources.ic_onboarding_1
import ebbingplanner.shared.generated.resources.ic_onboarding_2
import ebbingplanner.shared.generated.resources.ic_onboarding_3
import ebbingplanner.shared.generated.resources.ic_onboarding_4

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    // Android와 동일: 상단 Spacer 없음, 페이저 bottom=40, 인디케이터 bottom=30, 버튼 top=12/bottom=10
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 40.dp),
        ) { page ->
            OnboardingPageContent(page = page)
        }

        OnboardingIndicator(
            pageCount = 4,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(bottom = 30.dp),
        )

        EbbingSolidButton(
            label = if (pagerState.currentPage == 3) stringResource(Res.string.onboarding_start) else stringResource(Res.string.onboarding_next),
            onClick = {
                if (pagerState.currentPage == 3) {
                    viewModel.onIntent(OnboardingIntent.OnStartClick)
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp),
        )
    }
}

@Composable
private fun OnboardingPageContent(
    page: Int,
    modifier: Modifier = Modifier,
) {
    val (imageRes, title, description) = when (page) {
        0 -> Triple(
            Res.drawable.ic_onboarding_1,
            stringResource(Res.string.onboarding_page1_title),
            stringResource(Res.string.onboarding_page1_desc)
        )
        1 -> Triple(
            Res.drawable.ic_onboarding_2,
            stringResource(Res.string.onboarding_page2_title),
            stringResource(Res.string.onboarding_page2_desc)
        )
        2 -> Triple(
            Res.drawable.ic_onboarding_3,
            stringResource(Res.string.onboarding_page3_title),
            stringResource(Res.string.onboarding_page3_desc)
        )
        else -> Triple(
            Res.drawable.ic_onboarding_4,
            stringResource(Res.string.onboarding_page4_title),
            stringResource(Res.string.onboarding_page4_desc)
        )
    }

    // Android와 동일: Box(CenterStart) + 좌측정렬 텍스트, 이미지만 중앙, 제목 bottom=12
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                // Android와 동일: 온보딩 일러스트를 primary 색으로 틴트 + Inside 스케일
                colorFilter = ColorFilter.tint(EbbingTheme.colors.primaryDefault),
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .padding(vertical = 66.dp)
                    .size(300.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Text(
                text = title,
                style = EbbingTheme.typography.headingLSB,
                color = EbbingTheme.colors.black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Text(
                text = description,
                style = EbbingTheme.typography.bodyMM,
                color = EbbingTheme.colors.dark3,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun OnboardingIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(
                        width = if (isSelected) 20.dp else 8.dp,
                        height = 8.dp,
                    )
                    .clip(CircleShape)
                    .background(
                        if (isSelected) EbbingTheme.colors.dark3
                        else EbbingTheme.colors.light1
                    ),
            )
        }
    }
}
