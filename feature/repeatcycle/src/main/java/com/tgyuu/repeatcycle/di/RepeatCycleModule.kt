package com.tgyuu.repeatcycle.di

import com.tgyuu.repeatcycle.graph.addrepeatcycle.AddRepeatCycleViewModel
import com.tgyuu.repeatcycle.graph.editrepeatcycle.EditRepeatCycleViewModel
import com.tgyuu.repeatcycle.graph.main.RepeatCycleViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val repeatCycleModule = module {
    viewModelOf(::AddRepeatCycleViewModel)
    viewModelOf(::EditRepeatCycleViewModel)
    viewModelOf(::RepeatCycleViewModel)
}
