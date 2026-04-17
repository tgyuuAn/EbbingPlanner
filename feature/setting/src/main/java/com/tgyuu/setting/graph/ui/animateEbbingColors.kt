package com.tgyuu.setting.graph.ui

import androidx.compose.runtime.Composable
import com.tgyuu.common.util.ebbingAnimateColorAsState
import com.tgyuu.designsystem.foundation.EbbingColors

@Composable
internal fun animateEbbingColors(target: EbbingColors): EbbingColors {
    return EbbingColors(
        background       = ebbingAnimateColorAsState(target.background),
        primaryNormal    = ebbingAnimateColorAsState(target.primaryNormal),
        primaryDeep      = ebbingAnimateColorAsState(target.primaryDeep),
        primaryContainer = ebbingAnimateColorAsState(target.primaryContainer),
        statusError      = ebbingAnimateColorAsState(target.statusError),
        statusSuccess    = ebbingAnimateColorAsState(target.statusSuccess),
        textOnBackground = ebbingAnimateColorAsState(target.textOnBackground),
        textSub          = ebbingAnimateColorAsState(target.textSub),
        textDisabled     = ebbingAnimateColorAsState(target.textDisabled),
        textOnPrimary    = ebbingAnimateColorAsState(target.textOnPrimary),
        textPrimary      = ebbingAnimateColorAsState(target.textPrimary),
        textError        = ebbingAnimateColorAsState(target.textError),
        fillNormal       = ebbingAnimateColorAsState(target.fillNormal),
        fillStrong       = ebbingAnimateColorAsState(target.fillStrong),
        fillDisabled     = ebbingAnimateColorAsState(target.fillDisabled),
        fillPrimary      = ebbingAnimateColorAsState(target.fillPrimary),
    )
}
