package com.tgyuu.common.ui.resource

import android.content.Context
import androidx.annotation.StringRes

class ResourceProviderImpl(
    private val context: Context,
) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        context.getString(resId, *formatArgs)
}
