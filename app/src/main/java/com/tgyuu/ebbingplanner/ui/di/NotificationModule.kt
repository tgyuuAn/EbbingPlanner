package com.tgyuu.ebbingplanner.ui.di

import com.tgyuu.alarm.NotificationHelper
import com.tgyuu.ebbingplanner.ui.alarm.NotificationHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationDi {
    @Binds
    @Singleton
    abstract fun bindsNotificationHelper(
        notificationHelperImpl: NotificationHelperImpl
    ): NotificationHelper
}
