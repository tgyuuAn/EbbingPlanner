package com.tgyuu.deviceinfo.di

import com.tgyuu.deviceinfo.DeviceInfoProvider
import com.tgyuu.deviceinfo.DeviceInfoProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeviceInfoModule {

    @Binds
    @Singleton
    abstract fun bindsDeviceInfoProvider(
        impl: DeviceInfoProviderImpl,
    ): DeviceInfoProvider
}
