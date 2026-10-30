package com.tgyuu.shared.domain

import com.tgyuu.shared.common.currentInstant
import com.tgyuu.shared.domain.model.TodoSchedule
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ErrorRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/** 날짜 단위 로컬 알림 등록/취소 추상화 (iOS UNUserNotificationCenter 어댑터, 테스트 대역 주입용) */
interface DailyAlarmScheduler {
    fun schedule(id: Int, title: String, message: String, hour: Int, minute: Int, date: LocalDate)
    fun cancel(id: Int)
    fun cancelAll()
}

/**
 * Android는 날짜당 알람 1개만 걸고 발화 시점(TodoAlarmReceiver + NotificationHelperImpl)에
 * 그 날의 미완료 일정을 읽어 내용을 결정한다. iOS 로컬 알림은 발화 시점에 코드를 실행할 수 없으므로,
 * 일정이 바뀔 때마다 영향을 받은 날짜의 알림을 다시 계산해 등록한다.
 *
 * - 날짜당 알림 1개(id = 날짜 기반 고정값), 그 날 미완료·미삭제 일정이 없으면 취소
 * - 제목 = notification_content_title, 본문 = 알림 문구의 모든 플레이스홀더 토큰을 첫 일정 제목으로 치환
 * - 첫 일정 = 고정(isPinned) 우선, 나머지는 DB 순서 유지(Android sortedByDescending { isPinned }와 동일)
 * - 알림 설정이 꺼져 있으면 전부 취소
 *
 * 화면 ViewModel이 저장 직후 사라져도 재예약이 끊기지 않도록 앱 수명 스코프에서 직렬 실행한다.
 */
class AlarmRescheduler(
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val scheduler: DailyAlarmScheduler,
    private val titleProvider: suspend () -> String,
    private val defaultMessageProvider: suspend () -> String,
    private val errorRepository: ErrorRepository? = null,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
    private val timeZone: () -> TimeZone = { TimeZone.currentSystemDefault() },
    private val now: () -> Instant = { currentInstant() },
) {
    private val mutex = Mutex()
    private var rescheduleAllJob: Job? = null

    /** 영향을 받은 날짜들의 알림만 다시 계산한다 (오늘 이전 날짜는 무시). */
    fun rescheduleDates(dates: Collection<LocalDate>) {
        val targets = dates.toSet()
        if (targets.isEmpty()) return
        scope.launch { runLogged { rescheduleDatesNow(targets) } }
    }

    /** 전체 미래 날짜의 알림을 다시 계산한다 (앱 시작, 설정 변경, 동기화/복원 완료 시). */
    fun rescheduleAll() {
        // 알림 문구 입력처럼 연속 호출되는 경우를 짧게 합친다. 결과는 항상 DB/설정 최신값 기준이라 순서와 무관하다.
        rescheduleAllJob?.cancel()
        rescheduleAllJob = scope.launch {
            delay(RESCHEDULE_ALL_DEBOUNCE_MS)
            runLogged { rescheduleAllNow() }
        }
    }

    /** 예약된 알림을 모두 취소한다 (알림 OFF, 데이터 초기화). */
    fun cancelAll() {
        scope.launch { runLogged { mutex.withLock { scheduler.cancelAll() } } }
    }

    suspend fun rescheduleDatesNow(dates: Collection<LocalDate>) = mutex.withLock {
        if (!isEnabled()) {
            scheduler.cancelAll()
            return@withLock
        }
        val config = loadConfig()
        val today = today()
        dates.toSet().filter { it >= today }.forEach { date ->
            applyDate(date, todoRepository.loadSchedulesByDate(date), config)
        }
    }

    suspend fun rescheduleAllNow() = mutex.withLock {
        scheduler.cancelAll()
        if (!isEnabled()) return@withLock

        val config = loadConfig()
        val today = today()
        todoRepository.loadUpcomingSchedules(today)
            .groupBy { it.date }
            .entries
            .sortedBy { it.key }
            .asSequence()
            .filter { (_, list) -> list.any { !it.isDone } }
            // iOS는 앱당 대기 알림을 64개까지만 유지하므로 가까운 날짜부터 등록한다.
            .take(MAX_PENDING_DAYS)
            .forEach { (date, list) -> applyDate(date, list, config) }
    }

    private suspend fun applyDate(date: LocalDate, schedules: List<TodoSchedule>, config: AlarmConfig) {
        val id = notificationIdOf(date)
        val first = firstUndoneTitle(schedules)
        val fireAt = date.atTime(config.hour, config.minute).toInstant(timeZone())
        if (first == null || fireAt <= now()) {
            scheduler.cancel(id)
            return
        }
        scheduler.schedule(
            id = id,
            title = config.title,
            message = ConfigRepository.resolveAlarmMessage(config.template, first),
            hour = config.hour,
            minute = config.minute,
            date = date,
        )
    }

    private suspend fun isEnabled(): Boolean = configRepository.getNotificationEnabled().first()

    private suspend fun loadConfig(): AlarmConfig {
        val (hour, minute) = configRepository.getAlarmTime()
        val stored = configRepository.getAlarmMessage()
        // 저장 sentinel(한국어 기본 문구)은 화면과 같게 로케일별 기본 문구로 보여준다.
        val template = if (stored.isBlank() || stored == ConfigRepository.DEFAULT_ALARM_MESSAGE) {
            defaultMessageProvider()
        } else {
            stored
        }
        return AlarmConfig(hour = hour, minute = minute, title = titleProvider(), template = template)
    }

    private fun today(): LocalDate = now().toLocalDateTime(timeZone()).date

    private suspend fun runLogged(block: suspend () -> Unit) {
        try {
            block()
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Throwable) {
            errorRepository?.logError(e)
        }
    }

    private data class AlarmConfig(val hour: Int, val minute: Int, val title: String, val template: String)

    companion object {
        const val MAX_PENDING_DAYS = 60
        private const val RESCHEDULE_ALL_DEBOUNCE_MS = 300L

        /** 날짜별 고정 알림 id (yyyyMMdd). 앱 재시작 후에도 같은 값이라 취소/덮어쓰기가 정확하다. */
        fun notificationIdOf(date: LocalDate): Int =
            date.year * 10000 + date.monthNumber * 100 + date.dayOfMonth

        /** Android TodoAlarmReceiver와 동일: 미완료만, 고정 우선(안정 정렬) 후 첫 일정 제목 */
        fun firstUndoneTitle(schedules: List<TodoSchedule>): String? =
            schedules.filter { !it.isDone }
                .sortedByDescending { it.isPinned }
                .firstOrNull()
                ?.title
    }
}
