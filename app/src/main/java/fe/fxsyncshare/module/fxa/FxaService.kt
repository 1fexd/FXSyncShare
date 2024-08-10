package fe.fxsyncshare.module.fxa

import androidx.lifecycle.LifecycleOwner
import fe.android.lifecycle.LifecycleService
import fe.fxsyncshare.FXSyncShareApp
import fe.fxsyncshare.extension.koin.service
import fe.fxsyncshare.shortcut.ShortcutUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mozilla.components.browser.storage.sync.PlacesHistoryStorage
import mozilla.components.concept.sync.*
import mozilla.components.lib.fetch.httpurlconnection.HttpURLConnectionClient
import mozilla.components.service.fxa.PeriodicSyncConfig
import mozilla.components.service.fxa.SyncConfig
import mozilla.components.service.fxa.SyncEngine
import mozilla.components.service.fxa.manager.FxaAccountManager
import mozilla.components.service.fxa.sync.GlobalSyncableStoreProvider
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.sink.AndroidLogSink
import mozilla.components.support.rusthttp.RustHttpConfig
import mozilla.components.support.rustlog.RustLog
import org.koin.dsl.module

val firefoxSyncModule = module {
    service<FxaService> {
        FxaService(
            applicationContext,
            FxaUtil.defaultDeviceName(applicationContext),
            FxaServerConfig.DemoReleaseClient
        )
    }
}

class FxaService(
    val applicationContext: FXSyncShareApp,
    val deviceName: String,
    val config: FxaServerConfig,
) : LifecycleService {
    companion object {
        val entrypoint = object : FxAEntryPoint {
            override val entryName: String = "main"
        }
    }

    val accountManager by lazy {
        FxaAccountManager(
            context = applicationContext,
            serverConfig = config.toServerConfig(),
            deviceConfig = DeviceConfig(
                name = deviceName,
                type = DeviceType.MOBILE,
                capabilities = setOf(DeviceCapability.SEND_TAB),
                secureStateAtRest = true
            ),
            syncConfig = SyncConfig(
                supportedEngines = setOf(SyncEngine.History),
                periodicSyncConfig = PeriodicSyncConfig(periodMinutes = 15, initialDelayMinutes = 5),
            ),
        )
    }
    private val historyStorage = lazy { PlacesHistoryStorage(applicationContext) }

    init {
        RustLog.enable()
        RustHttpConfig.setClient(lazy { HttpURLConnectionClient() })

        Log.addSink(AndroidLogSink())
        GlobalSyncableStoreProvider.configureStore(SyncEngine.History to historyStorage)
    }

    private val _accountEvents = MutableStateFlow<AccountEvent>(AccountEvent.Waiting)
    val accountEvents = _accountEvents.asStateFlow()

    private val accountSyncObserver = AccountSyncObserver()
    private val accountEventObserver = AccountEventObserver(_accountEvents)

    val syncStatus = accountSyncObserver.state

    val oauthAccount = accountEventObserver.oauthAccount
    val profile = accountEventObserver.profile

    override suspend fun onAppInitialized(owner: LifecycleOwner) {
        accountManager.registerForSyncEvents(accountSyncObserver, owner, true)
        accountManager.register(accountEventObserver, owner, true)

        accountManager.start()
    }

    override suspend fun onStop() {
    }

    fun publishShortcuts(constellation: ConstellationState) {
        val success = ShortcutUtil.publishShortcuts(applicationContext, constellation.otherDevices)
        android.util.Log.d("Shortcuts", "$success")
    }

    fun pushShortcut(device: Device, direction: ShortcutUtil.Direction) {
        ShortcutUtil.pushShortcut(applicationContext, device, direction)
    }
}
