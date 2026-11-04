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
    val heading12SB: TextStyle = TextStyle(
        fontFamily = PretendardSemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
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

/**
 * 태블릿 토큰 모드 타이포그래피. Figma 태블릿 프레임은 Fontsize/Line-height 토큰이 한 단계씩 커요.
 * (xs 12→14, s 14→16, m 16→18, l 18→20, xl 20→22, xxl 24→28, xxxl 28→32)
 */
internal val TabletTypography: EbbingTypography = EbbingTypography().run {
    copy(
        heading28B = heading28B.copy(fontSize = 32.sp, lineHeight = 40.sp),
        heading24B = heading24B.copy(fontSize = 28.sp, lineHeight = 32.sp),
        heading20B = heading20B.copy(fontSize = 22.sp, lineHeight = 24.sp),
        heading18B = heading18B.copy(fontSize = 20.sp, lineHeight = 24.sp),
        heading16B = heading16B.copy(fontSize = 18.sp, lineHeight = 22.sp),
        heading14B = heading14B.copy(fontSize = 16.sp, lineHeight = 20.sp),
        heading16SB = heading16SB.copy(fontSize = 18.sp, lineHeight = 22.sp),
        heading14SB = heading14SB.copy(fontSize = 16.sp, lineHeight = 20.sp),
        heading12SB = heading12SB.copy(fontSize = 14.sp, lineHeight = 18.sp),
        body18M = body18M.copy(fontSize = 20.sp, lineHeight = 24.sp),
        body16M = body16M.copy(fontSize = 18.sp, lineHeight = 24.sp),
        body14M = body14M.copy(fontSize = 16.sp, lineHeight = 22.sp),
        caption14R = caption14R.copy(fontSize = 16.sp, lineHeight = 22.sp),
        caption12R = caption12R.copy(fontSize = 14.sp, lineHeight = 18.sp),
    )
}
