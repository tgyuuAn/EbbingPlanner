package com.tgyuu.ebbingplanner.widget.designsystem.foundation

import androidx.compose.ui.unit.sp
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle

/**
 * 위젯용 타이포그래피. EbbingTheme.typography와 동일한 스펙을 Glance TextStyle로 제공합니다.
 * Glance FontWeight는 Normal / Medium / Bold만 지원하므로 SemiBold → Bold로 근사합니다.
 */
object EbbingWidgetTypography {
    // Heading - Bold
    val heading28B = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold)
    val heading24B = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
    val heading20B = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
    val heading18B = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
    val heading16B = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
    val heading14B = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)

    // Heading - SemiBold (Glance 미지원 → Bold 근사)
    val heading16SB = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
    val heading14SB = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)

    // Body - Medium
    val body18M = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium)
    val body16M = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
    val body14M = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)

    // Caption - Regular
    val caption14R = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal)
    val caption12R = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
}
