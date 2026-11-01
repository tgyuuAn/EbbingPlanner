package com.tgyuu.home.print

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.Html
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tgyuu.designsystem.model.TodoScheduleUiModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * PoC: 선택한 날짜의 공부 범위를 메모지처럼 인쇄한다.
 * 시스템 인쇄 화면을 띄우므로 실제 프린터뿐 아니라 "PDF로 저장"도 가능하다.
 */
internal object SchedulePrinter {

    // 인쇄 작업이 레이아웃을 마칠 때까지 WebView가 GC되지 않도록 참조를 잡아둔다.
    private var printWebView: WebView? = null

    fun print(
        context: Context,
        date: LocalDate,
        schedules: List<TodoScheduleUiModel>,
        schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    ) {
        val html = buildHtml(date, schedules, schedulesByTodoInfo)
        val webView = WebView(context)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                val jobName = "EbbingPlanner_${date}"
                printManager.print(
                    jobName,
                    view.createPrintDocumentAdapter(jobName),
                    PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A6)
                        .build(),
                )
                printWebView = null
            }
        }
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        printWebView = webView
    }

    internal fun buildHtml(
        date: LocalDate,
        schedules: List<TodoScheduleUiModel>,
        schedulesByTodoInfo: Map<Int, List<TodoScheduleUiModel>>,
    ): String {
        val weekday = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        val sections = schedules
            .sortedWith(compareByDescending<TodoScheduleUiModel> { it.isPinned }.thenBy { it.createdAt })
            .groupBy { it.name }

        val body = if (schedules.isEmpty()) {
            "<p class=\"empty\">-</p>"
        } else {
            sections.entries.joinToString("") { (tagName, items) ->
                val color = String.format("#%06X", items.first().color and 0xFFFFFF)
                val rows = items.joinToString("") { item ->
                    val round = schedulesByTodoInfo[item.infoId]
                        ?.sortedBy { it.date }
                        ?.let { all ->
                            val index = all.indexOfFirst { it.id == item.id }
                            if (index >= 0 && all.size > 1) "${index + 1}/${all.size}" else null
                        }
                    val memo = item.memo.originalText.takeIf { it.isNotBlank() }
                    """
                    <li class="${if (item.isDone) "done" else ""}">
                      <span class="box">${if (item.isDone) "&#10003;" else ""}</span>
                      <div class="text">
                        <div class="title">${item.title.originalText.escape()}${round?.let { " <span class=\"round\">$it</span>" } ?: ""}</div>
                        ${memo?.let { "<div class=\"memo\">${it.escape()}</div>" } ?: ""}
                      </div>
                    </li>
                    """.trimIndent()
                }
                """
                <section>
                  <h2><span class="dot" style="background:$color"></span>${tagName.escape()} <span class="count">${items.size}</span></h2>
                  <ul>$rows</ul>
                </section>
                """.trimIndent()
            }
        }

        return """
        <!doctype html>
        <html><head><meta charset="utf-8">
        <style>
          @page { margin: 8mm; }
          body { font-family: sans-serif; color: #111; margin: 0; }
          h1 { font-size: 20px; margin: 0 0 4px; }
          .summary { font-size: 12px; color: #555; margin-bottom: 12px; }
          h2 { font-size: 14px; margin: 14px 0 6px; display: flex; align-items: center; gap: 6px; }
          .dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block;
                 -webkit-print-color-adjust: exact; print-color-adjust: exact; }
          .count { color: #888; font-weight: normal; }
          ul { list-style: none; padding: 0; margin: 0; }
          li { display: flex; gap: 8px; padding: 6px 0; border-bottom: 1px dashed #ccc; page-break-inside: avoid; }
          .box { flex: none; width: 14px; height: 14px; border: 1.5px solid #333; border-radius: 3px;
                 font-size: 12px; line-height: 14px; text-align: center; margin-top: 2px; }
          .title { font-size: 15px; }
          .round { font-size: 11px; color: #666; border: 1px solid #aaa; border-radius: 8px; padding: 0 5px; }
          .memo { font-size: 12px; color: #555; margin-top: 2px; white-space: pre-wrap; }
          li.done .title { text-decoration: line-through; color: #888; }
          .empty { color: #888; }
        </style></head>
        <body>
          <h1>${date.monthValue}/${date.dayOfMonth} ($weekday)</h1>
          <div class="summary">${schedules.count { it.isDone }}/${schedules.size}</div>
          $body
        </body></html>
        """.trimIndent()
    }

    private fun String.escape(): String = Html.escapeHtml(this)
}
