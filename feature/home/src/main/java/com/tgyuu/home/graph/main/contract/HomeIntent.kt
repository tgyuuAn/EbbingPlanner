package com.tgyuu.home.graph.main.contract

import com.tgyuu.common.base.UiIntent
import com.tgyuu.common.event.BottomSheetContent
import com.tgyuu.domain.model.SortType
import com.tgyuu.domain.model.TodoSchedule
import java.time.LocalDate

sealed interface HomeIntent : UiIntent {
    data class OnClickAddTodo(val selectedDate: LocalDate) : HomeIntent
    data class OnCheckChanged(val schedule: TodoSchedule) : HomeIntent
    data class OnClickSortType(val content: BottomSheetContent) : HomeIntent
    data class OnUpdateSortType(val sortType: SortType) : HomeIntent
    data class OnClickEditSchedule(val content: BottomSheetContent) : HomeIntent
    data class OnClickDeleteSchedule(val content: BottomSheetContent) : HomeIntent
    data class OnClickDeleteSingle(val schedule: TodoSchedule) : HomeIntent
    data class OnClickDeleteRemaining(val schedule: TodoSchedule) : HomeIntent
    data class OnClickUpdate(val schedule: TodoSchedule) : HomeIntent
    data class OnClickDelaySchedule(val schedule: TodoSchedule) : HomeIntent
    data class OnClickMemo(val schedule: TodoSchedule) : HomeIntent
    data class OnClickDeleteMemo(val schedule: TodoSchedule) : HomeIntent
    data object OnClickSync : HomeIntent
}
