package com.tgyuu.shared.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_monitor_t
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_queue_create

/**
 * NWPathMonitor 기반 네트워크 상태 (Android ConnectivityManager.NetworkCallback 대응).
 * 앱 생명주기 동안 하나만 만들어 계속 감시한다(Koin single).
 */
class IosNetworkMonitor : NetworkMonitor {
    private val _networkState = MutableStateFlow(NetworkState.None)
    override val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    private val monitor: nw_path_monitor_t = nw_path_monitor_create()

    init {
        nw_path_monitor_set_update_handler(monitor) { path ->
            _networkState.value = if (nw_path_get_status(path) == nw_path_status_satisfied) {
                NetworkState.Connected
            } else {
                NetworkState.NotConnected
            }
        }
        nw_path_monitor_set_queue(
            monitor,
            // attr null = serial queue
            dispatch_queue_create("com.tgyuu.ebbingplanner.network-monitor", null),
        )
        nw_path_monitor_start(monitor)
    }
}

actual fun createNetworkMonitor(): NetworkMonitor = IosNetworkMonitor()

