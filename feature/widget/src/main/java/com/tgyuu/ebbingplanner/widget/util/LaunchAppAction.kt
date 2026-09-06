package com.tgyuu.ebbingplanner.widget.util

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

/**
 * 위젯 클릭 시 앱의 메인 런처 액티비티를 실행하는 ActionCallback.
 * feature/widget 모듈에서 app 모듈의 MainActivity를 직접 참조할 수 없으므로,
 * 패키지의 launch intent를 사용한다.
 */
class LaunchAppAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        context.startActivity(launchIntent)
    }
}
