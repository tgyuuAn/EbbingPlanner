package com.tgyuu.ebbingplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgyuu.analytics.AnalyticsHelper
import com.tgyuu.common.suspendRunCatching
import com.tgyuu.domain.model.ErrorBus
import com.tgyuu.domain.model.Theme
import com.tgyuu.domain.model.UpdateInfo
import com.tgyuu.domain.model.UpdateState
import com.tgyuu.domain.repository.ConfigRepository
import com.tgyuu.domain.repository.SyncRepository
import com.tgyuu.domain.repository.TodoRepository
import com.tgyuu.navigation.NavigationBus
import com.tgyuu.navigation.NavigationEvent
import com.tgyuu.navigation.OnboardingRoute
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val configRepository: ConfigRepository,
    private val syncRepository: SyncRepository,
    private val todoRepository: TodoRepository,
    private val navigationBus: NavigationBus,
    private val errorBus: ErrorBus,
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() {
    private val softUpdateInfo = MutableStateFlow<UpdateInfo?>(null)
    private val hardUpdateInfo = MutableStateFlow<UpdateInfo?>(null)

    val updateState: StateFlow<UpdateState> = combine(
        softUpdateInfo,
        hardUpdateInfo
    ) { soft, hard ->
        UpdateState(soft = soft, hard = hard)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UpdateState()
    )

    val theme: StateFlow<Theme> = configRepository.getAppTheme()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Theme.NORMAL
        )

    suspend fun initAppState() = coroutineScope {
        val getSoftUpdateInfoJob = launch { getSoftUpdateInfo() }
        val getHardUpdateInfoJob = launch { getHardUpdateInfo() }
        val insertDefaultTagJob = launch { insertDefaultTag() }
        val checkOnboardingJob = launch { isFirstAppOpen() }
        val ensureUUIDExistsJob = launch { ensureUUIDExists() }
        val clearSyncJob = launch { clearSync() }

        getSoftUpdateInfoJob.join()
        getHardUpdateInfoJob.join()
        insertDefaultTagJob.join()
        checkOnboardingJob.join()
        ensureUUIDExistsJob.join()
        clearSyncJob.join()

        // UUID가 없을경우, 생성 이후 호출해야 하므로 동기적으로 호출
        setUserId()
    }

    private suspend fun isFirstAppOpen() {
        val isFirstAppOpen = configRepository.isFirstAppOpen()
        if (isFirstAppOpen) {
            navigationBus.navigate(NavigationEvent.TopLevelTo(OnboardingRoute))
        }
    }

    private suspend fun getSoftUpdateInfo() {
        suspendRunCatching {
            configRepository.getSoftUpdateInfo()
        }.onSuccess { softUpdateInfo.value = it }
    }

    private suspend fun getHardUpdateInfo() {
        suspendRunCatching {
            configRepository.getHardUpdateInfo()
        }.onSuccess { hardUpdateInfo.value = it }
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

    private suspend fun clearSync() {
        val shouldClear = configRepository.getClearSyncFlag()
        if (shouldClear) syncRepository.disconnectAnother()
    }
}
