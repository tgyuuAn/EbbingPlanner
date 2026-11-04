package com.tgyuu.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.tgyuu.designsystem.R
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

@Immutable
data class UpdateInfoUiModel(
    val minVersion: String,
    val noticeMsgs: ImmutableMap<String, String> = persistentMapOf(),
) {
    @Composable
    fun localizedNoticeMsg(): String {
        val language = LocalConfiguration.current.locales[0].language
        return noticeMsgs[language]?.takeIf { it.isNotBlank() }
            ?: stringResource(R.string.update_default_notice)
    }
}

@Immutable
data class UpdateStateUiModel(
    val soft: UpdateInfoUiModel? = null,
    val hard: UpdateInfoUiModel? = null,
)
