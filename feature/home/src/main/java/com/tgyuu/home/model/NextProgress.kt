package com.tgyuu.home.model

/**
 * 지난 일정 제목에서 이어서 학습할 다음 진도를 추정해요.
 *
 * - 범위: "경제원론 1-50" → "경제원론 51~" (끝은 사용자가 이어서 입력)
 * - 단일 숫자: "민법총론 2강" → "민법총론 3강"
 * - 숫자 없음: "영단어" → "영단어"
 *
 * @property title 제목 칸에 채워 줄 다음 진도 제목
 * @property subject 진도를 뺀 과목명. 저장 후 안내 토스트에 써요.
 * @property start 다음 진도의 시작 표현("51", "3강"). 진도를 알 수 없으면 null
 */
internal data class NextProgress(
    val title: String,
    val subject: String,
    val start: String?,
)

private val RANGE_REGEX = Regex("""(\d+)\s*[-~]\s*(\d+)""")
private val OPEN_RANGE_REGEX = Regex("""\d+\s*~\s*$""")
private val NUMBER_REGEX = Regex("""\d+""")
private val UNIT_REGEX = Regex("""^[^\s\d]*""")

internal fun suggestNextProgress(title: String): NextProgress {
    val trimmed = title.trim()

    RANGE_REGEX.findAll(trimmed).lastOrNull()?.let { match ->
        val end = match.groupValues[2].toLongOrNull() ?: return trimmed.asSameProgress()
        val prefix = trimmed.substring(0, match.range.first)
        val next = (end + 1).toString()
        return NextProgress(
            title = "$prefix$next~",
            subject = prefix.trimEnd(),
            start = next,
        )
    }

    // "경제원론 51~"처럼 끝을 비워 둔 범위는 어디까지 했는지 알 수 없어 그대로 이어가요.
    if (OPEN_RANGE_REGEX.containsMatchIn(trimmed)) return trimmed.asSameProgress()

    NUMBER_REGEX.findAll(trimmed).lastOrNull()?.let { match ->
        val number = match.value.toLongOrNull() ?: return trimmed.asSameProgress()
        val next = (number + 1).toString().padStart(match.value.length, '0')
        val suffix = trimmed.substring(match.range.last + 1)
        val unit = UNIT_REGEX.find(suffix)?.value.orEmpty()
        return NextProgress(
            title = trimmed.replaceRange(match.range, next),
            subject = trimmed.substring(0, match.range.first).trimEnd(),
            start = next + unit,
        )
    }

    return trimmed.asSameProgress()
}

private fun String.asSameProgress() = NextProgress(title = this, subject = this, start = null)

/** 마지막 글자에 받침이 있으면 true. 한글이 아니면 숫자 읽기 기준으로 판단해요. */
internal fun String.hasFinalConsonant(): Boolean {
    val last = lastOrNull() ?: return false
    return when (last) {
        in '가'..'힣' -> (last - '가') % 28 != 0
        // 영(0) 일(1) 삼(3) 육(6) 칠(7) 팔(8)
        in '0'..'9' -> last in "013678"
        else -> false
    }
}
