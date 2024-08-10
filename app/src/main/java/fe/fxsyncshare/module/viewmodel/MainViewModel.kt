package fe.fxsyncshare.module.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import fe.fxsyncshare.module.fxa.FxaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.*
import mozilla.components.service.fxa.FxaAuthData
import mozilla.components.service.fxa.sync.SyncReason
import mozilla.components.service.fxa.toAuthType

class MainViewModel(val fxaService: FxaService) : ViewModel(), DeviceConstellationObserver {
    suspend fun refreshDevices() = withContext(Dispatchers.IO) {
        fxaService.accountManager.authenticatedAccount()?.deviceConstellation()?.refreshDevices()
    }

    private val _deviceConstellationState = MutableStateFlow<ConstellationState?>(null)
    val deviceConstellationState = _deviceConstellationState.asStateFlow()

    fun test(){
//        fxaService.accountManager.start()
    }

    fun registerDeviceObserver(account: OAuthAccount, owner: LifecycleOwner) {
        account.deviceConstellation().registerDeviceObserver(
            observer = this,
            owner = owner,
            autoPause = true
        )
    }

    suspend fun syncNow(reason: SyncReason = SyncReason.Startup) = withContext(Dispatchers.IO) {
        fxaService.accountManager.syncNow(reason)
    }

    override fun onDevicesUpdate(constellation: ConstellationState) {
        _deviceConstellationState.tryEmit(constellation)
        fxaService.publishShortcuts(constellation)
    }
}
