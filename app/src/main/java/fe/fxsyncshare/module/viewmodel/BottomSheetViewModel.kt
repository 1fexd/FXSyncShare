package fe.fxsyncshare.module.viewmodel

import fe.composekit.theme.preference.ThemePreferences
import fe.fxsyncshare.module.fxa.AccountEvent
import fe.fxsyncshare.module.fxa.FxaService
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import fe.fxsyncshare.module.preference.app.AppPreferences
import fe.fxsyncshare.module.viewmodel.base.BaseViewModel
import fe.fxsyncshare.shortcut.ShortcutUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.*


class BottomSheetViewModel(
    private val preferenceRepository: AppPreferenceRepository,
    private val firefoxSync: FxaService,
) : BaseViewModel(preferenceRepository), DeviceConstellationObserver {
    val theme = preferenceRepository.asState(ThemePreferences.theme)
    val themeMaterialYou = preferenceRepository.asState(ThemePreferences.themeMaterialYou)
    val themeAmoled = preferenceRepository.asState(ThemePreferences.themeAmoled)

    private val deviceConstellation: DeviceConstellation?
        get() = firefoxSync.accountManager.authenticatedAccount()?.deviceConstellation()

    suspend fun refreshDevices() = withContext(Dispatchers.IO) {
        deviceConstellation?.refreshDevices()
    }

    suspend fun fetchDeviceConstellation() = withContext(Dispatchers.IO) {
        deviceConstellation ?: firefoxSync.accountEvents.filterIsInstance<AccountEvent.Ready>()
            .mapNotNull { it.authenticatedAccount?.deviceConstellation() }
            .firstOrNull()
    }

    private val _deviceConstellationFlow = MutableStateFlow<ConstellationState?>(null)
    val deviceConstellationFlow = _deviceConstellationFlow.asStateFlow()

    override fun onDevicesUpdate(constellation: ConstellationState) {
        _deviceConstellationFlow.tryEmit(constellation)
    }

    suspend fun sendTab(device: Device, tab: DeviceCommandOutgoing.SendTab) = withContext(Dispatchers.IO) {
        firefoxSync.pushShortcut(device, ShortcutUtil.Direction.Send)
        deviceConstellation?.sendCommandToDevice(device.id, tab)
    }
}
