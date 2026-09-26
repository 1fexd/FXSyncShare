package fe.fxsyncshare.module.viewmodel

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import fe.composekit.theme.Theme
import fe.composekit.theme.preference.ThemeHolder
import fe.composekit.theme.preference.ThemePreferences
import fe.fxsyncshare.module.fxa.FxaService
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.ConstellationState
import mozilla.components.concept.sync.DeviceConstellationObserver
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.service.fxa.sync.SyncReason

class MainViewModel(
    val fxaService: FxaService,
    val preferenceRepository: AppPreferenceRepository
) : ViewModel(), DeviceConstellationObserver, ThemeHolder {
    override val theme = preferenceRepository.asViewModelState(ThemePreferences.theme)
    override val themeAmoled  = preferenceRepository.asViewModelState(ThemePreferences.themeAmoled)
    override val themeMaterialYou = preferenceRepository.asViewModelState(ThemePreferences.themeMaterialYou)

    init {
        Log.d("MainViewModel", "init()")
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

    suspend fun syncNow(reason: SyncReason = SyncReason.Startup) = withContext(Dispatchers.IO) {
        fxaService.accountManager.syncNow(reason, customEngineSubset = emptyList())
    }

    override fun onDevicesUpdate(constellation: ConstellationState) {
        _deviceConstellationState.tryEmit(constellation)
        fxaService.publishShortcuts(constellation)
    }

    fun publishShortcuts(): Boolean {
        val const = fxaService.accountManager.authenticatedAccount()?.deviceConstellation()?.state()
        Log.d("MainViewModel", "${const}")
        val value = const ?: return false
        return fxaService.publishShortcuts(value)
    }

    fun toggleTheme() {
        val currentTheme = theme.value
        val newTheme = when(currentTheme) {
            Theme.Light -> Theme.Dark
            Theme.Dark -> Theme.Light
            Theme.System -> Theme.Light
        }
        theme.invoke(newTheme)
    }
}
