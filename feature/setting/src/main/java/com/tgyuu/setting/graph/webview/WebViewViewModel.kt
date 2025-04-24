package com.puzzle.setting.graph.webview

import androidx.lifecycle.ViewModel
import com.tgyuu.navigation.NavigationBus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    internal val navigationBus: NavigationBus,
) : ViewModel()
