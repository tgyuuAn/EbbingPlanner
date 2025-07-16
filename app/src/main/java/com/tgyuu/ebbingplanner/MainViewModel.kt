package com.tgyuu.ebbingplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.OnboardingRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configRepository: ConfigRepository,
    private val syncRepository: SyncRepository,
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {
    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo = _updateInfo.asStateFlow()

    val theme: StateFlow<Theme> = configRepository.getAppTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Theme.NORMAL
        )

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
    }

    private suspend fun isFirstAppOpen() {
        val isFirstAppOpen = configRepository.isFirstAppOpen()
        if (isFirstAppOpen) {
            navigationBus.navigate(NavigationEvent.TopLevelTo(OnboardingRoute))
        }
    }

    private suspend fun getUpdateInfo() {
        suspendRunCatching {
            configRepository.getUpdateInfo()
        }.onSuccess { _updateInfo.value = it }
    }

    private suspend fun insertDefaultTag() {
        todoRepository.addDefaultTag()
    }

    private suspend fun ensureUUIDExists() {
        syncRepository.ensureUUIDExists()
    }

    private suspend fun setUserId() {
        val uuid = syncRepository.getUuid()
        errorBus.setUserId(uuid)
        analyticsHelper.setUserId(uuid)
    }
}
