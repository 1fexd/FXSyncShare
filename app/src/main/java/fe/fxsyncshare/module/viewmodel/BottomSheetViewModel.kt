package fe.fxsyncshare.module.viewmodel

import androidx.lifecycle.ViewModel
import fe.composekit.theme.preference.ThemeHolder
import fe.composekit.theme.preference.ThemePreferences
import fe.fxsyncshare.module.fxa.AccountEvent
import fe.fxsyncshare.module.fxa.FxaService
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import fe.fxsyncshare.shortcut.ShortcutUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.ConstellationState
import mozilla.components.concept.sync.Device
import mozilla.components.concept.sync.DeviceCommandOutgoing
import mozilla.components.concept.sync.DeviceConstellation
import mozilla.components.concept.sync.DeviceConstellationObserver
import mozilla.components.support.base.log.logger.Logger


class BottomSheetViewModel(
    private val preferenceRepository: AppPreferenceRepository,
    private val firefoxSync: FxaService,
) : ViewModel(), DeviceConstellationObserver, ThemeHolder {
    private val logger = Logger("BottomSheetViewModel")

    override val theme = preferenceRepository.asViewModelState(ThemePreferences.theme)
    override val themeAmoled  = preferenceRepository.asViewModelState(ThemePreferences.themeAmoled)
    override val themeMaterialYou = preferenceRepository.asViewModelState(ThemePreferences.themeMaterialYou)

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
        deviceConstellation?.state()?.let {
            firefoxSync.publishShortcuts(it)
        }
        firefoxSync.pushShortcut(device, ShortcutUtil.Direction.Send)
        deviceConstellation?.sendCommandToDevice(device.id, tab)
    }
}
