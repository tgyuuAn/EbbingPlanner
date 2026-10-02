package com.tgyuu.dashboard.di

import com.tgyuu.dashboard.ScheduleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val scheduleModule = module {
    viewModelOf(::ScheduleViewModel)
}
