package com.tgyuu.setting.graph.ui

import androidx.compose.runtime.Composable
import com.tgyuu.common.util.ebbingAnimateColorAsState
import com.tgyuu.designsystem.foundation.EbbingColors

@Composable
internal fun animateEbbingColors(target: EbbingColors): EbbingColors {
    return EbbingColors(
        background = ebbingAnimateColorAsState(target.background),
        primaryDefault = ebbingAnimateColorAsState(target.primaryDefault),
        primaryMiddle = ebbingAnimateColorAsState(target.primaryMiddle),
        primaryLight = ebbingAnimateColorAsState(target.primaryLight),
        black = ebbingAnimateColorAsState(target.black),
        dark1 = ebbingAnimateColorAsState(target.dark1),
        dark2 = ebbingAnimateColorAsState(target.dark2),
        dark3 = ebbingAnimateColorAsState(target.dark3),
        light1 = ebbingAnimateColorAsState(target.light1),
        light2 = ebbingAnimateColorAsState(target.light2),
        light3 = ebbingAnimateColorAsState(target.light3),
        white = ebbingAnimateColorAsState(target.white),
        error = ebbingAnimateColorAsState(target.error),
        success = ebbingAnimateColorAsState(target.success),
    )
}
