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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tgyuu.shared.designsystem.component.EbbingSolidButton
import com.tgyuu.shared.designsystem.foundation.EbbingTheme
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            OnboardingPageContent(page = page)
        }

        OnboardingIndicator(
            pageCount = 4,
            currentPage = pagerState.currentPage,
        )

        Spacer(modifier = Modifier.height(32.dp))

        EbbingSolidButton(
            label = if (pagerState.currentPage == 3) "시작하기" else "다음",
            onClick = {
                if (pagerState.currentPage == 3) {
                    viewModel.onIntent(OnboardingIntent.OnStartClick)
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingPageContent(
    page: Int,
    modifier: Modifier = Modifier,
) {
    val (icon, title, description) = when (page) {
        0 -> Triple(
            Icons.Filled.DateRange,
            "에빙하우스 망각 곡선 기반 학습",
            "망각 곡선에 맞춰 최적의 복습 시점을\n자동으로 알려드려요."
        )
        1 -> Triple(
            Icons.Filled.CheckCircle,
            "간편한 일정 체크",
            "홈 화면에서 간편하게\n오늘의 일정을 확인하고 완료해보세요."
        )
        2 -> Triple(
            Icons.Filled.Notifications,
            "알림으로 놓치지 않게",
            "설정한 시간에 오늘의 할 일을\n알림으로 알려드려요."
        )
        else -> Triple(
            Icons.Filled.Refresh,
            "여러 기기에서 동기화",
            "다른 기기와 연결하여\n일정을 함께 관리해보세요."
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(EbbingTheme.colors.light3),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EbbingTheme.colors.primaryDefault,
                modifier = Modifier.size(100.dp),
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = title,
            style = EbbingTheme.typography.headingLSB,
            color = EbbingTheme.colors.black,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            style = EbbingTheme.typography.bodyMM,
            color = EbbingTheme.colors.dark2,
            textAlign = TextAlign.Center,
        )
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
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isSelected) EbbingTheme.colors.dark2
                        else EbbingTheme.colors.light1
                    ),
            )
        }
    }
}
