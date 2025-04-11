package com.tgyuu.ebbingplanner.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tgyuu.common.event.BottomSheetComposable
import com.tgyuu.designsystem.foundation.EbbingTheme
import com.tgyuu.ebbingplanner.ui.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            EbbingTheme {
                val navController = rememberNavController()
                var bottomSheetContent by remember { mutableStateOf<BottomSheetComposable?>(null) }
                val sheetState = rememberModalBottomSheetState(
                    initialValue = ModalBottomSheetValue.Hidden,
                    skipHalfExpanded = true,
                )

                val currentDestination = navController.currentBackStackEntryAsState()
                    .value?.destination

                Scaffold(
                    containerColor = EbbingTheme.colors.background,
//                    bottomBar = {
//                        EbbingBottomBarAnimation(
//                            visible = currentDestination?.shouldHideBottomBar() == false,
//                            modifier = Modifier.navigationBarsPadding(),
//                        ) {
//                            AppBottomBar(
//                                currentDestination = currentDestination,
//                                navigateToBottomNaviDestination = navigateToBottomNaviDestination,
//                            )
//                        }
//                    },
                    floatingActionButtonPosition = FabPosition.Center,
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
