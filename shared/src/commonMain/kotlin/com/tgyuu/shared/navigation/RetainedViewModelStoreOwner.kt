package com.tgyuu.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate

/**
 * 스택 화면(child) 하나의 ViewModel 보관소.
 *
 * Decompose Children은 백스택 화면의 컴포지션을 버리므로 remember로 만든 ViewModel은 복귀 시 새로 만들어져요.
 * Android의 백스택 엔트리 ViewModel처럼, child의 InstanceKeeper에 보관해 화면이 스택에 있는 동안 유지하고
 * child가 스택에서 제거되면(onDestroy) clear해 viewModelScope를 취소해요.
 */
class RetainedViewModelStoreOwner : InstanceKeeper.Instance, ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()

    override fun onDestroy() {
        viewModelStore.clear()
    }
}

internal fun ComponentContext.retainedViewModelStoreOwner(): RetainedViewModelStoreOwner =
    instanceKeeper.getOrCreate { RetainedViewModelStoreOwner() }

/** child 보관소에 ViewModel을 1개 만들어 두고, 같은 child로 다시 그려질 때 그대로 돌려줘요 */
@Composable
inline fun <reified VM : ViewModel> retainedViewModel(
    owner: ViewModelStoreOwner,
    noinline create: () -> VM,
): VM = remember(owner) {
    ViewModelProvider.create(
        owner = owner,
        factory = viewModelFactory { initializer { create() } },
    )[VM::class]
}
