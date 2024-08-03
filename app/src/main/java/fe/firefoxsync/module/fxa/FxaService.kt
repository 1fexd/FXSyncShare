package fe.firefoxsync.module.fxa

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
import mozilla.components.concept.sync.*
import mozilla.components.lib.fetch.httpurlconnection.HttpURLConnectionClient
import mozilla.components.service.fxa.manager.FxaAccountManager
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.sink.AndroidLogSink
import mozilla.components.support.rusthttp.RustHttpConfig
import mozilla.components.support.rustlog.RustLog
import org.koin.dsl.module

val firefoxSyncModule = module {
    service<FxaService> {
        FxaService(
            applicationContext,
            applicationLifecycle.coroutineScope,
            FxaUtil.defaultDeviceName(applicationContext),
            FxaServerConfig.DemoReleaseClient
        )
    }
}

class FxaService(
    val applicationContext: FirefoxSyncApp,
    val coroutineScope: LifecycleCoroutineScope,
    val deviceName: String,
    val config: FxaServerConfig,
) : Service {
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
            syncConfig = null
        )
    }

    init {
        RustLog.enable()
        RustHttpConfig.setClient(lazy { HttpURLConnectionClient() })

        Log.addSink(AndroidLogSink())
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

    fun publishShortcuts(constellation: ConstellationState) {
        val success = ShortcutUtil.publishShortcuts(applicationContext, constellation.otherDevices)
        android.util.Log.d("Shortcuts", "$success")
    }

    fun pushShortcut(device: Device, direction: ShortcutUtil.Direction) {
        ShortcutUtil.pushShortcut(applicationContext, device, direction)
    }
}
