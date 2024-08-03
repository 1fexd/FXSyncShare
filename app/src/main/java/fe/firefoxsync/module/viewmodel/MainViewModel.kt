package fe.firefoxsync.module.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import fe.firefoxsync.module.fxa.FxaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.ConstellationState
import mozilla.components.concept.sync.DeviceConstellationObserver
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.service.fxa.FxaAuthData
import mozilla.components.service.fxa.toAuthType

class MainViewModel(val fxaService: FxaService) : ViewModel(), DeviceConstellationObserver {
    val redirectUrl = fxaService.config.redirectUrl

    suspend fun startLogin() = withContext(Dispatchers.IO) {
        fxaService.accountManager.beginAuthentication(entrypoint = FxaService.entrypoint)
    }

    suspend fun finishLogin(code: String, state: String, action: String) = withContext(Dispatchers.IO) {
        fxaService.accountManager.finishAuthentication(FxaAuthData(action.toAuthType(), code = code, state = state))
    }

    suspend fun refreshDevices() = withContext(Dispatchers.IO) {
        fxaService.accountManager.authenticatedAccount()?.deviceConstellation()?.refreshDevices()
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
        fxaService.publishShortcuts(constellation)
    }
}
