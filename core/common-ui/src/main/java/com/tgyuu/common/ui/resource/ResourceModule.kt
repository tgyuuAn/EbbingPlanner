package com.tgyuu.common.ui.resource

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val resourceModule = module {
    single { ResourceProviderImpl(androidContext()) } bind ResourceProvider::class
}
