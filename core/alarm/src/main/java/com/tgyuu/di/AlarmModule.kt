package com.tgyuu.di

import android.app.AlarmManager
import android.content.Context
import com.tgyuu.alarm.AlarmInitializer
import com.tgyuu.alarm.AlarmRescheduler
import com.tgyuu.alarm.AlarmScheduler
import com.tgyuu.common.initializer.Initializer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module

val alarmModule = module {
    single<AlarmManager> { androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    single { AlarmScheduler(androidContext(), get()) }
    single { AlarmRescheduler(get(), get(), get()) }
    single { AlarmInitializer(get()) } bind Initializer::class
}
