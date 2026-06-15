package com.tgyuu.di

import android.app.AlarmManager
import android.content.Context
import com.tgyuu.alarm.AlarmInitializer
import com.tgyuu.common.initializer.Initializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlarmModule {

    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: Context
    ): AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmBindsModule {

    @Binds
    @IntoSet
    abstract fun bindAlarmInitializer(
        impl: AlarmInitializer,
    ): Initializer
}
