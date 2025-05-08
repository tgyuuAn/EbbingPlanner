package com.tgyuu.ebbingplanner.ui.di

import com.tgyuu.domain.repository.TodoRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun todoRepository(): TodoRepository
}
