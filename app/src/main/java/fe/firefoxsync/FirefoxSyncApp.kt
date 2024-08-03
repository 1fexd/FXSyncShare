package fe.firefoxsync

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import fe.firefoxsync.lifecycle.AppLifecycleObserver
import fe.firefoxsync.module.fxa.firefoxSyncModule
import fe.firefoxsync.module.viewmodel.module.viewModelModule
import fe.firefoxsync.extension.koin.androidApplicationContext
import fe.firefoxsync.extension.koin.applicationLifecycle
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
