package com.tgyuu.shared.designsystem.foundation

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
