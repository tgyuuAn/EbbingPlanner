package com.tgyuu.deviceinfo.di

import com.tgyuu.deviceinfo.DeviceInfoProvider
import com.tgyuu.deviceinfo.DeviceInfoProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val deviceInfoModule = module {
    single { DeviceInfoProviderImpl(androidContext()) } bind DeviceInfoProvider::class
}
