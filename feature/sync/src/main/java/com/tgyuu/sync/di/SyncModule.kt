package com.tgyuu.sync.di

import com.tgyuu.sync.graph.connect.ConnectViewModel
import com.tgyuu.sync.graph.main.SyncMainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val syncModule = module {
    viewModelOf(::SyncMainViewModel)
    viewModelOf(::ConnectViewModel)
}
