package com.tgyuu.ebbingplanner

import androidx.lifecycle.ViewModel
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.model.error.ErrorBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.OnboardingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val syncRepository: SyncRepository,
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
) : ViewModel() {
    private val _isInitialized = MutableStateFlow<Boolean>(true)
    val isInitialized = _isInitialized.asStateFlow()

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo = _updateInfo.asStateFlow()

    suspend fun initAppState() = coroutineScope {
        val getUpdateInfoJob = launch { getUpdateInfo() }
        val insertDefaultTagJob = launch { insertDefaultTag() }
        val checkOnboardingJob = launch { isFirstAppOpen() }
        val ensureUUIDExistsJob = launch { ensureUUIDExists() }

        getUpdateInfoJob.join()
        insertDefaultTagJob.join()
        checkOnboardingJob.join()
        ensureUUIDExistsJob.join()

        // UUID가 없을경우, 생성 이후 호출해야 하므로 동기적으로 호출
        setUserId()
        _isInitialized.value = false
    }

    private suspend fun isFirstAppOpen() {
        val isFirstAppOpen = configRepository.isFirstAppOpen()
        if (isFirstAppOpen) {
            navigationBus.navigate(NavigationEvent.TopLevelTo(OnboardingRoute))
        }
    }

    private suspend fun getUpdateInfo() {
        configRepository.getUpdateInfo()
            .onSuccess { _updateInfo.value = it }
    }

    private suspend fun insertDefaultTag() {
        todoRepository.addDefaultTag()
    }

    private suspend fun ensureUUIDExists() {
        syncRepository.ensureUUIDExists()
    }

    private suspend fun setUserId() {
        errorBus.setUserId(syncRepository.getUuid())
    }
}
