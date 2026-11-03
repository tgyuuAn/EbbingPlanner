package com.tgyuu.home.model

/**
 * 지난 일정 제목에서 이어서 학습할 다음 진도를 추정해요.
 *
 * 1. 같은 과목의 직전 제목이 있으면, 두 제목에서 바뀐 숫자만 같은 간격으로 이어가요.
 *    - "N3 단어 1-50" → "N3 단어 51-100" 다음은 "N3 단어 101-" (N3는 그대로)
 *    - "Day 1" → "Day 3" 다음은 "Day 5"
 * 2. 직전 제목이 없으면 제목 하나로 추정해요.
 *    - 범위는 시작점만 이어가고 끝은 비워요: "경제원론 1-50" → "경제원론 51-", "DAY 01-03" → "DAY 04-"
 *    - 숫자 하나는 1 늘려요: "민법총론 2강" → "민법총론 3강", "수능특강 3-1" → "수능특강 3-2"
 *    - 연도·급수·월·시간·분량·분모처럼 진도가 아닌 숫자는 건드리지 않아요.
 *
 * @property title 제목 칸에 채워 줄 다음 진도 제목
 * @property subject 진도 앞부분(과목명). 저장 후 안내 토스트에 써요.
 * @property start 다음 진도의 시작 표현("51", "3강"). 진도를 알 수 없으면 null
 * @property basis 어떤 근거로 추정했는지. 추천 품질 분석용 로그에 남겨요.
 */
internal data class NextProgress(
    val title: String,
    val subject: String,
    val start: String?,
    val basis: Basis,
) {
    enum class Basis(val logValue: String) {
        /** 같은 과목의 직전 제목과 비교 */
        HISTORY("history"),
        /** 제목 하나의 범위를 이어서 시작 */
        RANGE("range"),
        /** 제목 하나의 숫자를 1 증가 */
        NUMBER("number"),
        /** 진도를 알 수 없어 제목 그대로 */
        NONE("none"),
    }
}

/**
 * 숫자만 다른 제목을 같은 과목으로 묶기 위한 키.
 * "TOEIC 1-50", "toeic 51~100", 끝을 비운 "TOEIC 101-"는 모두 같은 키예요.
 */
internal fun progressKey(title: String): String = title.trim()
    .lowercase()
    .replace(NUMBER_REGEX, "#")
    .replace(RANGE_SEPARATOR_REGEX, "-")
    .replace(OPEN_RANGE_KEY_REGEX, "#-#")
    .replace(WHITESPACE_REGEX, " ")

internal fun suggestNextProgress(title: String, previousTitle: String? = null): NextProgress {
    val latest = title.trim()
    val tokens = numberTokens(latest)

    if (previousTitle != null && progressKey(previousTitle) == progressKey(latest)) {
        suggestFromHistory(latest, tokens, numberTokens(previousTitle.trim()))?.let { return it }
    }
    return suggestFromTitle(latest, tokens)
}

private data class NumberToken(val value: Long, val range: IntRange) {
    val width: Int get() = range.last - range.first + 1
}

private fun numberTokens(title: String): List<NumberToken> =
    NUMBER_REGEX.findAll(title).mapNotNull { match ->
        match.value.toLongOrNull()?.let { NumberToken(it, match.range) }
    }.toList()

private fun suggestFromHistory(
    title: String,
    tokens: List<NumberToken>,
    previousTokens: List<NumberToken>,
): NextProgress? {
    if (tokens.size != previousTokens.size) return null
    val changed = tokens.indices.filter { tokens[it].value != previousTokens[it].value }
    if (changed.isEmpty()) return null

    // 범위 자체가 바뀌었으면 마지막 범위 크기로 이어가요. (1-50 → 51-80 다음은 81-110)
    if (changed.size == 2 && changed[1] == changed[0] + 1 && isRange(title, tokens, changed[0])) {
        return continueRange(title, tokens, changed[0], NextProgress.Basis.HISTORY)
    }

    val replacements = changed.associateWith { index ->
        val step = tokens[index].value - previousTokens[index].value
        if (step <= 0) return null
        tokens[index].value + step
    }
    return buildProgress(title, tokens, replacements, NextProgress.Basis.HISTORY)
}

private fun suggestFromTitle(title: String, tokens: List<NumberToken>): NextProgress {
    // "경제원론 51-"처럼 끝을 비워 둔 범위는 어디까지 했는지 알 수 없어 그대로 이어가요.
    if (OPEN_RANGE_REGEX.containsMatchIn(title)) {
        return NextProgress(title = title, subject = title, start = null, basis = NextProgress.Basis.NONE)
    }

    val candidates = tokens.indices.filterNot { isNonProgress(title, tokens, it) }

    candidates.lastOrNull { index ->
        index + 1 in candidates && isRange(title, tokens, index)
    }?.let { return continueRange(title, tokens, it, NextProgress.Basis.RANGE) }

    val target = candidates.lastOrNull { hasProgressUnit(title, tokens[it]) }
        ?: candidates.lastOrNull()
        ?: return NextProgress(title = title, subject = title, start = null, basis = NextProgress.Basis.NONE)

    return buildProgress(title, tokens, mapOf(target to tokens[target].value + 1), NextProgress.Basis.NUMBER)
}

/** tokens[index]와 그 다음 숫자가 "A-B" / "A~B" 범위인지. "3-1"처럼 앞이 크거나 같으면 단원 표기로 봐요. */
private fun isRange(title: String, tokens: List<NumberToken>, index: Int): Boolean {
    val start = tokens[index]
    val end = tokens.getOrNull(index + 1) ?: return false
    val separator = title.substring(start.range.last + 1, end.range.first)
    return RANGE_SEPARATOR_REGEX.matches(separator) && start.value < end.value
}

/**
 * 범위는 끝을 비워 두고 시작점만 이어가요. 끝나는 지점은 매번 달라서 사용자가 채워요.
 * 구분자는 원래 쓴 것을 따르고, 끝에 붙은 단위는 끝과 함께 비워요. (1-50 → 51-, 1~10번 → 11~)
 */
private fun continueRange(
    title: String,
    tokens: List<NumberToken>,
    index: Int,
    basis: NextProgress.Basis,
): NextProgress {
    val start = tokens[index]
    val end = tokens[index + 1]
    val separator = title.substring(start.range.last + 1, end.range.first).trim()
    val nextStart = (end.value + 1).toString().padStart(start.width, '0')
    val afterEnd = title.substring(end.range.last + 1)
    val rest = afterEnd.substring(UNIT_REGEX.find(afterEnd)?.value?.length ?: 0)

    return NextProgress(
        title = title.substring(0, start.range.first) + nextStart + separator + rest,
        subject = subjectBefore(title, start),
        start = nextStart,
        basis = basis,
    )
}

private fun buildProgress(
    title: String,
    tokens: List<NumberToken>,
    replacements: Map<Int, Long>,
    basis: NextProgress.Basis,
): NextProgress {
    val nextTitle = StringBuilder(title)
    // 뒤에서부터 바꿔야 앞쪽 인덱스가 밀리지 않아요.
    replacements.keys.sortedDescending().forEach { index ->
        val token = tokens[index]
        nextTitle.replace(
            token.range.first,
            token.range.last + 1,
            replacements.getValue(index).toString().padStart(token.width, '0'),
        )
    }

    val firstIndex = replacements.keys.min()
    val firstToken = tokens[firstIndex]
    val start = replacements.getValue(firstIndex).toString().padStart(firstToken.width, '0')
    val unit = UNIT_REGEX.find(title.substring(firstToken.range.last + 1))?.value.orEmpty()

    return NextProgress(
        title = nextTitle.toString(),
        subject = subjectBefore(title, firstToken),
        start = start + unit,
        basis = basis,
    )
}

private fun subjectBefore(title: String, token: NumberToken): String =
    title.substring(0, token.range.first).trimEnd().trimEnd { !it.isLetterOrDigit() }

/** 연도, JLPT 급수, 월·분·시간 같은 단위, "10/20"의 분모처럼 진도가 아닌 숫자. */
private fun isNonProgress(title: String, tokens: List<NumberToken>, index: Int): Boolean {
    val token = tokens[index]
    val before = title.substring(0, token.range.first)
    val after = title.substring(token.range.last + 1)

    return (token.width == 4 && token.value in 1900..2099) ||
        LEVEL_PREFIX_REGEX.containsMatchIn(before) ||
        NON_PROGRESS_UNIT_REGEX.containsMatchIn(after) ||
        before.endsWith("/")
}

private fun hasProgressUnit(title: String, token: NumberToken): Boolean =
    PROGRESS_PREFIX_REGEX.containsMatchIn(title.substring(0, token.range.first)) ||
        PROGRESS_UNIT_REGEX.containsMatchIn(title.substring(token.range.last + 1))

private val NUMBER_REGEX = Regex("""\d+""")
private val RANGE_SEPARATOR_REGEX = Regex("""\s*[-~]\s*""")
private val OPEN_RANGE_REGEX = Regex("""\d+\s*[-~]\s*$""")
private val OPEN_RANGE_KEY_REGEX = Regex("""#-(?!#)""")
private val WHITESPACE_REGEX = Regex("""\s+""")
private val UNIT_REGEX = Regex("""^[^\s\d]*""")
private val LEVEL_PREFIX_REGEX = Regex("""(^|[^A-Za-z])[Nn]$""")
private val NON_PROGRESS_UNIT_REGEX = Regex("""^\s*(월|분|시간|초|년|급|문제|개|명|원|%)""")
private val PROGRESS_PREFIX_REGEX = Regex(
    """(?i)(p\.?|page|day|ch\.?|chapter|part|unit|lesson|lec|챕터|단원|제)\s*$"""
)
private val PROGRESS_UNIT_REGEX = Regex("""^\s*(강|장|회|과|편|번|단원|챕터|주차|일차|p\b)""")

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
