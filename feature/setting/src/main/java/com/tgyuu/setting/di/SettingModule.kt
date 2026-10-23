package com.tgyuu.setting.di

import com.tgyuu.setting.graph.main.SettingViewModel
import com.tgyuu.setting.graph.theme.ThemeViewModel
import com.tgyuu.setting.graph.webview.WebViewViewModel
import com.tgyuu.setting.graph.widget.WidgetViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val settingModule = module {
    viewModelOf(::SettingViewModel)
    viewModelOf(::ThemeViewModel)
    viewModelOf(::WidgetViewModel)
    viewModelOf(::WebViewViewModel)
}
