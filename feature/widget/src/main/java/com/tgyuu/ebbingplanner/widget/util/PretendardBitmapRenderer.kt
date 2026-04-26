package com.tgyuu.ebbingplanner.widget.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.core.content.res.ResourcesCompat
import com.tgyuu.designsystem.R
import java.io.File
import kotlin.math.ceil
import androidx.core.graphics.createBitmap

object PretendardBitmapRenderer {

    enum class Weight { BOLD, SEMI_BOLD, MEDIUM, REGULAR }

    private val typefaceCache = mutableMapOf<Weight, Typeface?>()

    private fun getTypeface(context: Context, weight: Weight): Typeface {
        return typefaceCache.getOrPut(weight) {
            val resId = when (weight) {
                Weight.BOLD -> R.font.pretendard_bold
                Weight.SEMI_BOLD -> R.font.pretendard_semi_bold
                Weight.MEDIUM -> R.font.pretendard_medium
                Weight.REGULAR -> R.font.pretendard_medium
            }
            runCatching { ResourcesCompat.getFont(context, resId) }.getOrNull()
        } ?: Typeface.DEFAULT
    }

    /**
     * 텍스트를 Pretendard 폰트로 렌더링한 Bitmap을 반환한다.
     *
     * @param maxWidthPx    줄바꿈 기준 너비(px). Int.MAX_VALUE 이면 텍스트 너비에 맞춘다.
     * @param maxLines      최대 줄 수 (초과 시 말줄임표)
     * @param strikethrough 취소선 여부
     */
    fun renderText(
        context: Context,
        text: String,
        weight: Weight,
        sizeSp: Float,
        textColorArgb: Int,
        maxWidthPx: Int = Int.MAX_VALUE,
        maxLines: Int = 1,
        strikethrough: Boolean = false,
    ): Bitmap {
        val dm = context.resources.displayMetrics
        @Suppress("DEPRECATION")
        val textSizePx = sizeSp * dm.scaledDensity

        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = getTypeface(context, weight)
            textSize = textSizePx
            color = textColorArgb
            isStrikeThruText = strikethrough
        }

        val effectiveWidth = if (maxWidthPx == Int.MAX_VALUE) {
            ceil(paint.measureText(text)).toInt().coerceAtLeast(1)
        } else {
            maxWidthPx
        }

        val layout = StaticLayout.Builder
            .obtain(text, 0, text.length, paint, effectiveWidth)
            .setMaxLines(maxLines)
            .setEllipsize(TextUtils.TruncateAt.END)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()

        val bitmapWidth = effectiveWidth.coerceAtLeast(1)
        val bitmapHeight = layout.height.coerceAtLeast(1)

        val bitmap = createBitmap(bitmapWidth, bitmapHeight)
        Canvas(bitmap).also { layout.draw(it) }
        return bitmap
    }

    /** 렌더링 결과를 내부 저장소 파일로 원자적으로 저장한다. */
    fun saveBitmap(context: Context, bitmap: Bitmap, filename: String): File {
        val dir = File(context.filesDir, "widget_bitmaps").also { it.mkdirs() }
        val target = File(dir, filename)
        val temp = File(dir, "$filename.tmp")
        temp.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        temp.renameTo(target)
        return target
    }

    /** 저장된 비트맵 파일을 불러온다. 파일이 없으면 null 반환. */
    fun loadBitmap(context: Context, filename: String): Bitmap? {
        val file = File(context.filesDir, "widget_bitmaps/$filename")
        if (!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }

    /** [renderText] 후 [saveBitmap]을 한 번에 수행한다. */
    fun renderAndSave(
        context: Context,
        text: String,
        weight: Weight,
        sizeSp: Float,
        textColorArgb: Int,
        filename: String,
        maxWidthPx: Int = Int.MAX_VALUE,
        maxLines: Int = 1,
        strikethrough: Boolean = false,
    ) {
        val bitmap = renderText(context, text, weight, sizeSp, textColorArgb, maxWidthPx, maxLines, strikethrough)
        try {
            saveBitmap(context, bitmap, filename)
        } finally {
            bitmap.recycle()
        }
    }
}
