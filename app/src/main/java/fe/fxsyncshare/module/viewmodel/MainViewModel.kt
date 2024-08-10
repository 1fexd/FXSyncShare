package fe.fxsyncshare.module.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import fe.composekit.theme.preference.ThemePreferences
import fe.fxsyncshare.module.fxa.FxaService
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import fe.fxsyncshare.module.preference.app.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.*
import mozilla.components.service.fxa.FxaAuthData
import mozilla.components.service.fxa.sync.SyncReason
import mozilla.components.service.fxa.toAuthType

class MainViewModel(val fxaService: FxaService, val preferenceRepository: AppPreferenceRepository) : ViewModel(), DeviceConstellationObserver {

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

    suspend fun syncNow(reason: SyncReason = SyncReason.Startup) = withContext(Dispatchers.IO) {
        fxaService.accountManager.syncNow(reason, customEngineSubset= emptyList())
    }

    override fun onDevicesUpdate(constellation: ConstellationState) {
        _deviceConstellationState.tryEmit(constellation)
        fxaService.publishShortcuts(constellation)
    }
}
