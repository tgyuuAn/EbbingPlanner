package com.tgyuu.data.di

import com.tgyuu.data.repository.ConfigRepositoryImpl
import com.tgyuu.data.repository.ErrorRepositoryImpl
import com.tgyuu.data.repository.FeatureFlagRepositoryImpl
import com.tgyuu.data.repository.SyncRepositoryImpl
import com.tgyuu.data.repository.TodoRepositoryImpl
import com.tgyuu.domain.model.Timer
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.ErrorRepository
import com.tgyuu.domain.repository.FeatureFlagRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { TodoRepositoryImpl(get(), get(), get()) } bind TodoRepository::class
    single { ConfigRepositoryImpl(get(), get(), get(), get()) } bind ConfigRepository::class
    single {
        SyncRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get())
    } bind SyncRepository::class
    single { ErrorRepositoryImpl(get()) } bind ErrorRepository::class
    single { FeatureFlagRepositoryImpl(get()) } bind FeatureFlagRepository::class
    single { Timer() }
}
