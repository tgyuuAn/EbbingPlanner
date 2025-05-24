package com.tgyuu.memo.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tgyuu.navigation.MemoRoute

fun NavGraphBuilder.memoNavigation() {
    composable<MemoRoute> {
        com.tgyuu.memo.MemoRoute()
    }
}
