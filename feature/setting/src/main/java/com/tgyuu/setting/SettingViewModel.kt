package com.tgyuu.setting

import com.tgyuu.common.base.BaseViewModel
import com.tgyuu.setting.contract.SettingIntent
import com.tgyuu.setting.contract.SettingState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor() :
    BaseViewModel<SettingState, SettingIntent>(SettingState()) {
    override suspend fun processIntent(intent: SettingIntent) {
        TODO("Not yet implemented")
    }
}
