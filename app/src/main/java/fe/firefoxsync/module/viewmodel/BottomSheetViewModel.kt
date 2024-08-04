package fe.firefoxsync.module.viewmodel

import androidx.lifecycle.ViewModel
import fe.firefoxsync.module.fxa.AccountEvent
import fe.firefoxsync.module.fxa.FxaService
import fe.firefoxsync.shortcut.ShortcutUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import mozilla.components.concept.sync.*


class BottomSheetViewModel(
    private val firefoxSync: FxaService,
) : ViewModel(), DeviceConstellationObserver {
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
