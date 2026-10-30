package com.tgyuu.shared.ui.feature.home.edittodo
import androidx.lifecycle.viewModelScope

import com.tgyuu.shared.base.BaseViewModel
import com.tgyuu.shared.domain.AlarmRescheduler
import com.tgyuu.shared.common.loadTagsByUsage
import com.tgyuu.shared.domain.repository.ConfigRepository
import com.tgyuu.shared.domain.repository.ExperimentRepository
import com.tgyuu.shared.domain.repository.TodoRepository
import com.tgyuu.shared.platform.AnalyticsEvent
import com.tgyuu.shared.platform.AnalyticsHelper
import com.tgyuu.shared.ui.model.TodoScheduleUiModel
import com.tgyuu.shared.ui.model.TodoTagUiModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import com.tgyuu.shared.domain.model.DefaultTodoTag
import ebbingplanner.shared.generated.resources.Res
import ebbingplanner.shared.generated.resources.tag_unassigned
import ebbingplanner.shared.generated.resources.snack_date_has_schedule
import ebbingplanner.shared.generated.resources.snack_required_fields
import ebbingplanner.shared.generated.resources.snack_todo_update_failed
import ebbingplanner.shared.generated.resources.snack_todo_updated
import org.jetbrains.compose.resources.getString

class EditTodoViewModel(
    private val scheduleId: Int,
    private val todoRepository: TodoRepository,
    private val configRepository: ConfigRepository,
    private val analyticsHelper: AnalyticsHelper? = null,
    private val onNavigateBack: () -> Unit,
    private val onNavigateToHome: (LocalDate) -> Unit = {},
    private val onNavigateToAddTag: () -> Unit = {},
    private val onShowSnackbar: (String) -> Unit = {},
    private val experimentRepository: ExperimentRepository? = null,
    private val alarmRescheduler: AlarmRescheduler? = null,
) : BaseViewModel<EditTodoState, EditTodoIntent>(EditTodoState()) {

    init {
        loadScheduleData()
        safeScope.launch {
            configRepository.getMondayStart().collect { setState { copy(mondayStart = it) } }
        }
    }

    private fun loadScheduleData() {
        safeScope.launch {
            val originSchedule = todoRepository.loadSchedule(scheduleId) ?: run {
                onNavigateBack()
                return@launch
            }

            val originTagDeferred = async { todoRepository.loadTag(originSchedule.tagId) }
            val sameInfoSchedulesDeferred =
                async { todoRepository.loadSchedulesByTodoInfo(originSchedule.infoId) }
            val todoInfoDeferred = async { todoRepository.loadTodoInfoById(originSchedule.infoId) }

            val originTag = originTagDeferred.await() ?: run {
                // Android과 동일: 태그를 찾을 수 없으면 뒤로가기(조용한 무동작 방지)
                onNavigateBack()
                return@launch
            }
            val schedulesByDateMap = sameInfoSchedulesDeferred.await()
            val todoInfo = todoInfoDeferred.await()

            val unassignedName = getString(Res.string.tag_unassigned)
            val tagModel = originTag.toUiModel()
                .let { if (it.id == DefaultTodoTag.id) it.copy(name = unassignedName) else it }

            setState {
                copy(
                    originSchedule = originSchedule,
                    schedulesByDateMap = schedulesByDateMap
                        .groupBy { it.date }
                        .mapValues { (_, list) -> list.map { it.toUiModel() }.toImmutableList() }
                        .toImmutableMap(),
                    selectedDate = originSchedule.date,
                    title = originSchedule.title,
                    isPinned = originSchedule.isPinned,
                    tag = tagModel,
                    restDays = todoInfo.restDays.toImmutableSet(),
                )
            }
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val unassignedName = getString(Res.string.tag_unassigned)
        val models = todoRepository.loadTagsByUsage(configRepository)
            .map { it.toUiModel() }
            .map { if (it.id == DefaultTodoTag.id) it.copy(name = unassignedName) else it }
            .toImmutableList()
        setState {
            copy(tagList = models)
        }
    }

    // Android 대응: 태그 추가 화면 복귀 시 방금 추가한 태그 자동 선택(recentAddedTagId는 읽으면 소비됨).
    fun loadNewTag() {
        val id = todoRepository.recentAddedTagId?.toInt() ?: return
        safeScope.launch {
            val newTag = todoRepository.loadTag(id) ?: return@launch
            setState { copy(tag = newTag.toUiModel()) }
        }
    }

    override suspend fun processIntent(intent: EditTodoIntent) {
        when (intent) {
            EditTodoIntent.OnBackClick -> onNavigateBack()
            is EditTodoIntent.OnSelectedDateChange -> onSelectedDateChange(intent.selectedDate)
            is EditTodoIntent.OnTitleChange -> onTitleChange(intent.title)
            is EditTodoIntent.OnPinnedChange -> setState { copy(isPinned = intent.isPinned) }
            is EditTodoIntent.OnTagChange -> setState { copy(tag = intent.tag) }
            EditTodoIntent.OnAddTagClick -> onNavigateToAddTag()
            EditTodoIntent.OnSaveClick -> onSaveClick()
        }
    }

    private fun onSelectedDateChange(date: LocalDate) {
        if (date == currentState.selectedDate) return

        val scheduledDates: Set<LocalDate> = currentState.schedulesByDateMap[date]
            ?.map { it.date }
            ?.toSet()
            ?: emptySet()

        if (date in scheduledDates) {
            viewModelScope.launch { onShowSnackbar(getString(Res.string.snack_date_has_schedule)) }
            return
        }

        setState { copy(selectedDate = date) }
    }

    private fun onTitleChange(title: String) {
        setState { copy(title = title) }
    }

    private suspend fun onSaveClick() {
        analyticsHelper?.logEvent(
            AnalyticsEvent(
                type = AnalyticsEvent.Types.BUTTON_CLICK,
                properties = mapOf(
                    AnalyticsEvent.PropertiesKeys.SCREEN_NAME to "EditTodo",
                    AnalyticsEvent.PropertiesKeys.BUTTON_NAME to "Save",
                ),
            )
        )

        if (!currentState.isSaveEnabled) {
            onShowSnackbar(getString(Res.string.snack_required_fields))
            return
        }

        val tag = currentState.tag ?: return
        val originSchedule = currentState.originSchedule ?: return
        val newSchedule = originSchedule.copy(
            title = currentState.title,
            date = currentState.selectedDate,
            tagId = tag.id,
            name = tag.name,
            color = tag.color,
            isPinned = currentState.isPinned,
        )

        try {
            todoRepository.updateTodo(newSchedule)
            todoRepository.updateTodoInfo(newSchedule, currentState.restDays.toSet())
            // 저장 완료 후 부가 기록 실패가 완료 흐름을 막지 않도록 격리
            runCatching { configRepository.recordTagUsage(tag.id) }

            // 제목/고정/날짜 변경 모두 알림 본문에 영향을 주므로 기존·새 날짜를 다시 계산한다.
            // (날짜 단위 알림이라 기존 날짜를 단순 취소하면 같은 날의 다른 일정 알림까지 사라짐)
            alarmRescheduler?.rescheduleDates(setOf(originSchedule.date, newSchedule.date))

            onShowSnackbar(getString(Res.string.snack_todo_updated))
            onNavigateToHome(currentState.selectedDate)
        } catch (e: Exception) {
            onShowSnackbar(getString(Res.string.snack_todo_update_failed))
        }
    }

    private fun com.tgyuu.shared.domain.model.TodoTag.toUiModel() = TodoTagUiModel(
        id = id,
        name = name,
        color = color,
        createdAt = createdAt,
    )

    private fun com.tgyuu.shared.domain.model.TodoSchedule.toUiModel() = TodoScheduleUiModel(
        id = id,
        infoId = infoId,
        date = date,
        title = title,
        tagId = tagId,
        name = name,
        color = color,
        isPinned = isPinned,
        isDone = isDone,
        memo = memo,
        createdAt = createdAt,
        infoCreatedAt = infoCreatedAt,
    )
}
