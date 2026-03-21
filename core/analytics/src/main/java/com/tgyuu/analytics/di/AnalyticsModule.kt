package com.tgyuu.analytics.di

import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.tgyuu.analytics.AmplitudeAnalyticsHelper
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.analytics.BuildConfig
import com.tgyuu.analytics.DebugAnalyticsHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val analyticsModule = module {
    single {
        Amplitude(
            Configuration(
                apiKey = BuildConfig.AMPLITUDE_API_KEY,
                context = androidContext(),
            )
        )
    }

    single<AnalyticsHelper>(named("debug")) { DebugAnalyticsHelper() }

    single<AnalyticsHelper>(named("release")) { AmplitudeAnalyticsHelper(get()) }

    single<AnalyticsHelper> {
        if (BuildConfig.DEBUG) {
            get(named("debug"))
        } else {
            get(named("release"))
        }
    }
}
