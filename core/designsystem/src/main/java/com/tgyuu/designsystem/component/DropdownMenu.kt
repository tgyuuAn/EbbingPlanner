package com.tgyuu.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.tgyuu.common.util.clickable
import com.tgyuu.designsystem.R
import com.tgyuu.designsystem.foundation.EbbingTheme

/** 팝업 창 안에서 그림자가 잘리지 않도록 콘텐츠 둘레에 두는 여백 */
private val DropdownShadowInset = 24.dp

/**
 * 태블릿에서 트리거(미트볼·드롭다운 필드) 바로 아래에 붙어 뜨는 메뉴.
 * 트리거를 감싼 Box 안에서 호출하면 그 Box를 기준으로 위치를 잡고, 아래 공간이 모자라면 위로 뒤집어요.
 *
 * @param alignEnd true면 트리거 오른쪽 끝에, false면 왼쪽 끝에 맞춰요
 * @param gap 트리거와 메뉴 사이 간격
 */
@Composable
fun EbbingDropdownPopup(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false,
    gap: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val density = LocalDensity.current
    val positionProvider = remember(alignEnd, gap, density) {
        DropdownPositionProvider(alignEnd = alignEnd, gap = gap, density = density)
    }

    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Box(modifier = Modifier.padding(DropdownShadowInset)) {
            Column(content = content, modifier = modifier)
        }
    }
}

private class DropdownPositionProvider(
    private val alignEnd: Boolean,
    private val gap: Dp,
    private val density: Density,
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val inset = with(density) { DropdownShadowInset.roundToPx() }
        val gapPx = with(density) { gap.roundToPx() }

        val x = if (alignEnd) anchorBounds.right - popupContentSize.width + inset
        else anchorBounds.left - inset

        val below = anchorBounds.bottom + gapPx - inset
        val above = anchorBounds.top - gapPx - popupContentSize.height + inset
        val y = if (below + popupContentSize.height <= windowSize.height || above < 0) below else above

        return IntOffset(
            x = x.coerceIn(0, (windowSize.width - popupContentSize.width).coerceAtLeast(0)),
            y = y.coerceAtLeast(0),
        )
    }
}

/** 일정 편집(미트볼) 메뉴 컨테이너. 디자인 Edit 컴포넌트: 흰 배경, radius 8, 넓게 퍼지는 그림자 */
@Composable
fun EbbingOptionsDropdown(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)

    EbbingDropdownPopup(
        onDismissRequest = onDismissRequest,
        alignEnd = true,
        modifier = modifier
            .width(IntrinsicSize.Max)
            .widthIn(min = 172.dp)
            .shadow(
                elevation = 20.dp,
                shape = shape,
                ambientColor = DropdownShadowColor,
                spotColor = DropdownShadowColor,
            )
            .clip(shape)
            .background(EbbingTheme.colors.background),
    ) {
        Text(
            text = title,
            style = EbbingTheme.typography.heading14B,
            color = EbbingTheme.colors.textOnBackground,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 14.dp),
        )

        content()
    }
}

@Composable
fun EbbingOptionsDropdownItem(
    label: String,
    onClick: () -> Unit,
) {
    HorizontalDivider(color = EbbingTheme.colors.strokeOutline, thickness = 1.dp)

    Text(
        text = label,
        style = EbbingTheme.typography.body14M,
        color = EbbingTheme.colors.textOnBackground,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 22.dp, vertical = 14.dp),
    )
}

/** 태그·반복 주기 선택 드롭다운 컨테이너. 필드와 같은 폭으로 필드 아래에 붙어요 */
@Composable
fun EbbingSelectDropdown(
    onDismissRequest: () -> Unit,
    width: Dp,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)

    EbbingDropdownPopup(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .width(width)
                .heightIn(max = 420.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = shape,
                    ambientColor = SelectShadowColor,
                    spotColor = SelectShadowColor,
                )
                .clip(shape)
                .background(EbbingTheme.colors.background)
                .border(1.dp, EbbingTheme.colors.strokeOutline, shape)
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            content = content,
        )
    }
}

@Composable
fun EbbingSelectDropdownItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Int? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) EbbingTheme.colors.fillSelected else EbbingTheme.colors.background)
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        if (color != null) {
            Spacer(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(color)),
            )
        }

        Text(
            text = label,
            style = EbbingTheme.typography.body18M,
            color = EbbingTheme.colors.textOnBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        if (selected) {
            Image(
                painter = painterResource(R.drawable.ic_textinput_check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(EbbingTheme.colors.primaryNormal),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(20.dp),
            )
        }
    }
}

/** 드롭다운 맨 아래 "+ 새 OO 추가" 행. 누르면 한 단계 더 들어가는 팝업을 열어요 */
@Composable
fun EbbingSelectDropdownAddItem(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 14.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = EbbingTheme.colors.fillPrimary,
            modifier = Modifier.size(20.dp),
        )

        Text(
            text = label,
            style = EbbingTheme.typography.body18M,
            color = EbbingTheme.colors.fillPrimary,
            maxLines = 1,
        )
    }
}

private val DropdownShadowColor = Color(0x3D21232A)
private val SelectShadowColor = Color(0x408994A8)
