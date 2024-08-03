package fe.firefoxsync.module.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import fe.firefoxsync.module.firefox.FirefoxSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.ConstellationState
import mozilla.components.concept.sync.DeviceConstellationObserver
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.service.fxa.FxaAuthData
import mozilla.components.service.fxa.toAuthType

class MainViewModel(val firefoxSync: FirefoxSyncService) : ViewModel(), DeviceConstellationObserver {
    suspend fun startLogin() = withContext(Dispatchers.IO) {
        firefoxSync.accountManager.beginAuthentication(entrypoint = FirefoxSyncService.entrypoint)
    }

    suspend fun finishLogin(code: String, state: String, action: String) = withContext(Dispatchers.IO) {
        firefoxSync.accountManager.finishAuthentication(FxaAuthData(action.toAuthType(), code = code, state = state))
    }

    private val _deviceConstellationState = MutableStateFlow<ConstellationState?>(null)
    val deviceConstellationState = _deviceConstellationState.asStateFlow()

    fun registerDeviceObserver(account: OAuthAccount, owner: LifecycleOwner) {
        account.deviceConstellation().registerDeviceObserver(
            observer = this,
            owner = owner,
            autoPause = true
        )
    }

    override fun onDevicesUpdate(constellation: ConstellationState) {
        _deviceConstellationState.tryEmit(constellation)
        firefoxSync.updateShareTargets(constellation)
    }

    suspend fun refreshDevices() = withContext(Dispatchers.IO) {
        firefoxSync.accountManager.authenticatedAccount()?.deviceConstellation()?.refreshDevices()
    }
}
