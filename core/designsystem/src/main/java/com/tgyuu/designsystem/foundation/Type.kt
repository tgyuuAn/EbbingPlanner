package com.tgyuu.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tgyuu.designsystem.R

private val PretendardBold = FontFamily(
    Font(
        resId = R.font.pretendard_bold,
        weight = FontWeight.Bold,
    ),
)

private val PretendardSemiBold = FontFamily(
    Font(
        resId = R.font.pretendard_semi_bold,
        weight = FontWeight.SemiBold,
    ),
)

private val PretendardMedium = FontFamily(
    Font(
        resId = R.font.pretendard_medium,
        weight = FontWeight.Medium,
    ),
)

private val PretendardRegular = FontFamily(
    Font(
        resId = R.font.pretendard_medium,
        weight = FontWeight.Normal,
    ),
)

@Immutable
data class EbbingTypography(
    // Heading — 한 페이지, 섹션의 제목/가장 중요한 텍스트
    val heading28B: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 28.sp,
        lineHeight = 40.sp,
    ),
    val heading24B: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    val heading20B: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
    val heading18B: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    val heading16B: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val heading14B: TextStyle = TextStyle(
        fontFamily = PretendardBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    val heading16SB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val heading14SB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    // Body — 본문 내에 쓰이는 일반 텍스트
    val body18M: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    val body16M: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val body14M: TextStyle = TextStyle(
        fontFamily = PretendardMedium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Caption — 본문 텍스트를 보조하는 하위 정보
    val caption14R: TextStyle = TextStyle(
        fontFamily = PretendardRegular,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val caption12R: TextStyle = TextStyle(
        fontFamily = PretendardRegular,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)
