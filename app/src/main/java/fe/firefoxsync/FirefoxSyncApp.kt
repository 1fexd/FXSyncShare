package fe.firefoxsync

import android.app.Application
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.ProcessLifecycleOwner
import fe.firefoxsync.lifecycle.AppLifecycleObserver
import fe.firefoxsync.module.firefox.firefoxSyncModule
import fe.firefoxsync.module.viewmodel.module.viewModelModule
import fe.firefoxsync.extension.koin.androidApplicationContext
import fe.firefoxsync.extension.koin.applicationLifecycle
import mozilla.components.browser.storage.sync.PlacesHistoryStorage
import mozilla.components.lib.fetch.httpurlconnection.HttpURLConnectionClient
import mozilla.components.service.fxa.SyncEngine
import mozilla.components.service.fxa.sync.GlobalSyncableStoreProvider
import mozilla.components.support.base.log.Log
import mozilla.components.support.base.log.sink.AndroidLogSink
import mozilla.components.support.rusthttp.RustHttpConfig
import mozilla.components.support.rustlog.RustLog
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FirefoxSyncApp : Application() {
    private lateinit var lifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()

        lifecycleObserver = AppLifecycleObserver(ProcessLifecycleOwner.get())
        lifecycleObserver.attach()

        val koinApplication = startKoin {
            androidLogger()
            androidApplicationContext<FirefoxSyncApp>(this@FirefoxSyncApp)
            applicationLifecycle(lifecycleObserver)
            modules(
                firefoxSyncModule,
                viewModelModule
            )
        }

        lifecycleObserver.dispatchAppInitialized()
    }
}
