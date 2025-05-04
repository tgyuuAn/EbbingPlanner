package com.tgyuu.ebbingplanner.ui

import androidx.lifecycle.ViewModel
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.OnboardingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
) : ViewModel() {
    internal suspend fun isFirstAppOpen() {
        val isFirstAppOpen = configRepository.isFirstAppOpen()
        if (isFirstAppOpen) {
            navigationBus.navigate(NavigationEvent.TopLevelTo(OnboardingRoute))
        }
    }

    internal suspend fun insertDefaultTag() {
        todoRepository.addDefaultTag()
    }
}
