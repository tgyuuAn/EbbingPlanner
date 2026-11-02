package com.tgyuu.home.print

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * PoC: 선택한 날짜의 공부 범위를 체크박스 목록 PDF로 만들어 공유 시트로 넘긴다.
 * 메모지처럼 출력해 책상에 붙여두는 용도라 A6 한 장 기준으로 그린다.
 */
internal object SchedulePrinter {

    suspend fun sharePdf(
        context: Context,
        date: LocalDate,
        schedules: List<TodoScheduleUiModel>,
        schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    ) {
        val file = withContext(Dispatchers.IO) {
            val dir = File(context.cacheDir, PDF_DIR).apply {
                deleteRecursively()
                mkdirs()
            }
            File(dir, "EbbingPlanner_${date}.pdf").also { file ->
                file.outputStream().use { out ->
                    createDocument(date, schedules, schedulesByTodoInfo).apply {
                        writeTo(out)
                        close()
                    }
                }
            }
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.print.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newRawUri(file.name, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, file.name))
    }

    private fun createDocument(
        date: LocalDate,
        schedules: List<TodoScheduleUiModel>,
        schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    ): PdfDocument {
        val document = PdfDocument()
        val painter = PagePainter(document)

        val weekday = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        painter.drawText("${date.monthValue}/${date.dayOfMonth} ($weekday)", titlePaint)
        painter.space(2f)
        painter.drawText("${schedules.count { it.isDone }}/${schedules.size}", subPaint)

        schedules
            .sortedWith(compareByDescending<TodoScheduleUiModel> { it.isPinned }.thenBy { it.createdAt })
            .groupBy { it.name }
            .forEach { (tagName, items) ->
                painter.space(SECTION_GAP)
                painter.drawTagHeader(tagName, items.first().color, items.size)
                items.forEach { item ->
                    val round = schedulesByTodoInfo[item.infoId]
                        ?.sortedBy { it.date }
                        ?.let { all ->
                            val index = all.indexOfFirst { it.id == item.id }
                            if (index >= 0 && all.size > 1) "${index + 1}/${all.size}" else null
                        }
                    painter.drawCheckItem(
                        title = item.title.originalText + (round?.let { "  ($it)" } ?: ""),
                        memo = item.memo.originalText.takeIf { it.isNotBlank() },
                        isDone = item.isDone,
                    )
                }
            }

        painter.finish()
        return document
    }

    /** 위에서 아래로 그려 내려가다 공간이 모자라면 새 페이지를 연다. */
    private class PagePainter(private val document: PdfDocument) {
        private var pageNumber = 0
        private var page: PdfDocument.Page = newPage()
        private val canvas: Canvas get() = page.canvas
        private var y = MARGIN

        private fun newPage(): PdfDocument.Page {
            pageNumber++
            return document.startPage(
                PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            )
        }

        private fun ensure(height: Float) {
            if (y + height <= PAGE_HEIGHT - MARGIN) return
            document.finishPage(page)
            page = newPage()
            y = MARGIN
        }

        fun space(height: Float) {
            y += height
        }

        fun drawText(text: String, paint: TextPaint) {
            val layout = layout(text, paint, CONTENT_WIDTH)
            ensure(layout.height.toFloat())
            canvas.save()
            canvas.translate(MARGIN, y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height
        }

        fun drawTagHeader(name: String, color: Int, count: Int) {
            val layout = layout("$name  $count", tagPaint, CONTENT_WIDTH - DOT_SIZE - 6f)
            ensure(layout.height + 4f)
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
            canvas.drawCircle(MARGIN + DOT_SIZE / 2, y + layout.height / 2f, DOT_SIZE / 2, dotPaint)
            canvas.save()
            canvas.translate(MARGIN + DOT_SIZE + 6f, y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height + 4f
        }

        fun drawCheckItem(title: String, memo: String?, isDone: Boolean) {
            val textX = MARGIN + BOX_SIZE + 8f
            val textWidth = CONTENT_WIDTH - BOX_SIZE - 8f
            val titleLayout = layout(title, if (isDone) doneItemPaint else itemPaint, textWidth)
            val memoLayout = memo?.let { layout(it, memoPaint, textWidth) }
            val height = titleLayout.height + (memoLayout?.height?.plus(2) ?: 0) + ITEM_PADDING * 2
            ensure(height)

            val top = y + ITEM_PADDING
            val boxTop = top + (titleLayout.getLineBottom(0) - BOX_SIZE) / 2f
            val box = RectF(MARGIN, boxTop, MARGIN + BOX_SIZE, boxTop + BOX_SIZE)
            canvas.drawRoundRect(box, 2f, 2f, boxPaint)
            if (isDone) {
                canvas.drawLine(box.left + 2.5f, box.centerY(), box.left + 5f, box.bottom - 2.5f, checkPaint)
                canvas.drawLine(box.left + 5f, box.bottom - 2.5f, box.right - 2f, box.top + 2.5f, checkPaint)
            }

            canvas.save()
            canvas.translate(textX, top)
            titleLayout.draw(canvas)
            memoLayout?.let {
                canvas.translate(0f, titleLayout.height + 2f)
                it.draw(canvas)
            }
            canvas.restore()

            y += height
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, dividerPaint)
        }

        fun finish() {
            document.finishPage(page)
        }

        private fun layout(text: String, paint: TextPaint, width: Float): StaticLayout =
            StaticLayout.Builder.obtain(text, 0, text.length, paint, width.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setIncludePad(false)
                .build()
    }

    private const val PDF_DIR = "print"

    // A6 (105 x 148mm) in PostScript points
    private const val PAGE_WIDTH = 298
    private const val PAGE_HEIGHT = 420
    private const val MARGIN = 20f
    private const val CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2
    private const val SECTION_GAP = 12f
    private const val ITEM_PADDING = 5f
    private const val BOX_SIZE = 10f
    private const val DOT_SIZE = 7f

    private fun textPaint(size: Float, color: Int, bold: Boolean = false) =
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = size
            this.color = color
            if (bold) typeface = Typeface.DEFAULT_BOLD
        }

    private val titlePaint = textPaint(16f, 0xFF111111.toInt(), bold = true)
    private val subPaint = textPaint(9f, 0xFF666666.toInt())
    private val tagPaint = textPaint(10.5f, 0xFF333333.toInt(), bold = true)
    private val itemPaint = textPaint(11f, 0xFF111111.toInt())
    private val doneItemPaint = textPaint(11f, 0xFF999999.toInt()).apply {
        isStrikeThruText = true
    }
    private val memoPaint = textPaint(8.5f, 0xFF666666.toInt())
    private val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1.2f
        color = 0xFF333333.toInt()
    }
    private val checkPaint = Paint(boxPaint).apply { strokeWidth = 1.4f }
    private val dividerPaint = Paint().apply {
        color = 0xFFDDDDDD.toInt()
        strokeWidth = 0.5f
    }
}
