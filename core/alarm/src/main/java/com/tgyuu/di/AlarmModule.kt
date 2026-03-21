package com.tgyuu.di

import android.app.AlarmManager
import android.content.Context
import com.tgyuu.alarm.AlarmScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val alarmModule = module {
    single<AlarmManager> { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    single { AlarmScheduler(androidContext(), get()) }
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
