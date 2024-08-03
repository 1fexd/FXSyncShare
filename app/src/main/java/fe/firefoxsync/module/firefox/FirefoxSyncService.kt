package fe.firefoxsync.module.firefox

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import fe.firefoxsync.FirefoxSyncApp
import fe.firefoxsync.extension.koin.service
import fe.firefoxsync.lifecycle.Service
import fe.firefoxsync.shortcut.ShortcutUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mozilla.appservices.fxaclient.FxaServer
import mozilla.components.browser.storage.sync.PlacesHistoryStorage
import mozilla.components.concept.sync.*
import mozilla.components.lib.dataprotect.SecureAbove22Preferences
import mozilla.components.lib.fetch.httpurlconnection.HttpURLConnectionClient
import mozilla.components.service.fxa.PeriodicSyncConfig
import mozilla.components.service.fxa.ServerConfig
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
    service<FirefoxSyncService> {
        FirefoxSyncService(applicationContext, applicationLifecycle.coroutineScope, "3c49430b43dfba77")
    }
}

class FirefoxSyncService(
    val applicationContext: FirefoxSyncApp,
    val coroutineScope: LifecycleCoroutineScope,
    val clientId: String,
) : Service {
    companion object {
        val entrypoint = object : FxAEntryPoint {
            override val entryName: String = "main"
        }
    }

    val redirectUrl = "https://accounts.firefox.com/oauth/success/$clientId"
    private val securePreferences by lazy { SecureAbove22Preferences(applicationContext, "key_store") }

    val accountManager by lazy {
        FxaAccountManager(
            applicationContext,
            ServerConfig(FxaServer.Release, clientId, redirectUrl),
            DeviceConfig(
                name = "FirefoxSyncShare",
                type = DeviceType.MOBILE,
                capabilities = setOf(DeviceCapability.SEND_TAB),
                secureStateAtRest = true,
            ),
            SyncConfig(
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

    private val accountEventObserver = AccountEventObserver(_accountEvents)

    override fun onAppInitialized(owner: LifecycleOwner) {
        accountManager.register(accountEventObserver, owner = owner, autoPause = true)
        coroutineScope.launch(Dispatchers.IO) { accountManager.start() }
    }

    override fun onStop(owner: LifecycleOwner) {
        accountManager.unregister(accountEventObserver)
    }

    fun updateShareTargets(constellation: ConstellationState) {
        val success = ShortcutUtil.publishShortcuts(applicationContext, constellation.otherDevices)
        android.util.Log.d("Shortcuts", "$success")
    }
}
