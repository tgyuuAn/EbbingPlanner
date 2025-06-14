package com.tgyuu.ebbingplanner.ui

import androidx.lifecycle.ViewModel
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.OnboardingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val syncRepository: SyncRepository,
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
) : ViewModel() {
    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo = _updateInfo.asStateFlow()

    internal suspend fun isFirstAppOpen() {
        val isFirstAppOpen = configRepository.isFirstAppOpen()
        if (isFirstAppOpen) {
            navigationBus.navigate(NavigationEvent.TopLevelTo(OnboardingRoute))
        }
    }

    internal suspend fun getUpdateInfo() {
        configRepository.getUpdateInfo()
            .onSuccess { _updateInfo.value = it }
    }

    internal suspend fun insertDefaultTag() {
        todoRepository.addDefaultTag()
    }

    internal suspend fun ensureUUIDExists() {
        syncRepository.ensureUUIDExists()
    }
}
