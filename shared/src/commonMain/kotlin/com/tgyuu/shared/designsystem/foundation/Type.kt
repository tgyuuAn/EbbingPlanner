package com.tgyuu.shared.designsystem.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.pretendard_bold
import ebbingplanner.shared.generated.resources.pretendard_medium
import ebbingplanner.shared.generated.resources.pretendard_semi_bold
import org.jetbrains.compose.resources.Font

@Immutable
data class EbbingTypography(
    // Heading — Bold
    val headingXLSB: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 40.sp,
    ),
    val headingLSB: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    val headingMSB: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 24.sp,
    ),
    val headingSSB: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    val headingSM: TextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp,
    ),
    // heading16B 대응
    val headingMB: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    // heading14B 대응
    val headingSB: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    // Body — SemiBold/Medium/Normal
    val bodyMSB: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val bodyMM: TextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val bodyMR: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    val bodySSB: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val bodySM: TextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val bodySR: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Caption
    val captionR: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    val captionM: TextStyle = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    val captionR12: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

/**
 * 태블릿 토큰 모드 타이포그래피 (Android TabletTypography와 동일 매핑).
 * Figma 태블릿 프레임은 Fontsize/Line-height 토큰이 한 단계씩 커요.
 */
internal val TabletTypography: EbbingTypography = EbbingTypography().run {
    copy(
        headingXLSB = headingXLSB.copy(fontSize = 32.sp, lineHeight = 40.sp),
        headingLSB = headingLSB.copy(fontSize = 28.sp, lineHeight = 32.sp),
        headingMSB = headingMSB.copy(fontSize = 22.sp, lineHeight = 24.sp),
        headingSSB = headingSSB.copy(fontSize = 20.sp, lineHeight = 24.sp),
        headingSM = headingSM.copy(fontSize = 20.sp, lineHeight = 24.sp),
        headingMB = headingMB.copy(fontSize = 18.sp, lineHeight = 22.sp),
        headingSB = headingSB.copy(fontSize = 16.sp, lineHeight = 20.sp),
        bodyMSB = bodyMSB.copy(fontSize = 18.sp, lineHeight = 24.sp),
        bodyMM = bodyMM.copy(fontSize = 18.sp, lineHeight = 24.sp),
        bodyMR = bodyMR.copy(fontSize = 18.sp, lineHeight = 24.sp),
        bodySSB = bodySSB.copy(fontSize = 16.sp, lineHeight = 22.sp),
        bodySM = bodySM.copy(fontSize = 16.sp, lineHeight = 22.sp),
        bodySR = bodySR.copy(fontSize = 16.sp, lineHeight = 22.sp),
        captionR = captionR.copy(fontSize = 16.sp, lineHeight = 22.sp),
        captionM = captionM.copy(fontSize = 14.sp, lineHeight = 18.sp),
        captionR12 = captionR12.copy(fontSize = 14.sp, lineHeight = 18.sp),
    )
}

/** 모든 스타일에 같은 폰트 패밀리를 적용 (크기/행간/굵기는 유지) */
private fun EbbingTypography.withFontFamily(fontFamily: FontFamily): EbbingTypography = copy(
    headingXLSB = headingXLSB.copy(fontFamily = fontFamily),
    headingLSB = headingLSB.copy(fontFamily = fontFamily),
    headingMSB = headingMSB.copy(fontFamily = fontFamily),
    headingSSB = headingSSB.copy(fontFamily = fontFamily),
    headingSM = headingSM.copy(fontFamily = fontFamily),
    headingMB = headingMB.copy(fontFamily = fontFamily),
    headingSB = headingSB.copy(fontFamily = fontFamily),
    bodyMSB = bodyMSB.copy(fontFamily = fontFamily),
    bodyMM = bodyMM.copy(fontFamily = fontFamily),
    bodyMR = bodyMR.copy(fontFamily = fontFamily),
    bodySSB = bodySSB.copy(fontFamily = fontFamily),
    bodySM = bodySM.copy(fontFamily = fontFamily),
    bodySR = bodySR.copy(fontFamily = fontFamily),
    captionR = captionR.copy(fontFamily = fontFamily),
    captionM = captionM.copy(fontFamily = fontFamily),
    captionR12 = captionR12.copy(fontFamily = fontFamily),
)

/**
 * Android core/designsystem Type.kt의 Pretendard 대응.
 * Compose Resources의 Font()는 @Composable이라 EbbingTheme 안에서 타이포그래피를 만들어요.
 * Android PretendardRegular는 pretendard_medium 파일을 Normal 굵기로 쓰므로 동일하게 매핑해요.
 */
@Composable
internal fun rememberEbbingTypography(isTablet: Boolean): EbbingTypography {
    val bold = Font(Res.font.pretendard_bold, FontWeight.Bold)
    val semiBold = Font(Res.font.pretendard_semi_bold, FontWeight.SemiBold)
    val medium = Font(Res.font.pretendard_medium, FontWeight.Medium)
    val regular = Font(Res.font.pretendard_medium, FontWeight.Normal)
    return remember(bold, semiBold, medium, regular, isTablet) {
        val fontFamily = FontFamily(bold, semiBold, medium, regular)
        (if (isTablet) TabletTypography else EbbingTypography()).withFontFamily(fontFamily)
    }
}
