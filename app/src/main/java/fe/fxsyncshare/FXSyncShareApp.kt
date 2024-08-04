package fe.fxsyncshare

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import fe.fxsyncshare.lifecycle.AppLifecycleObserver
import fe.fxsyncshare.module.fxa.firefoxSyncModule
import fe.fxsyncshare.module.viewmodel.module.viewModelModule
import fe.fxsyncshare.extension.koin.androidApplicationContext
import fe.fxsyncshare.extension.koin.applicationLifecycle
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FXSyncShareApp : Application() {
    private lateinit var lifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()

        lifecycleObserver = AppLifecycleObserver(ProcessLifecycleOwner.get())
        lifecycleObserver.attach()

        val koinApplication = startKoin {
            androidLogger()
            androidApplicationContext<FXSyncShareApp>(this@FXSyncShareApp)
            applicationLifecycle(lifecycleObserver)
            modules(
                firefoxSyncModule,
                viewModelModule
            )
        }

        lifecycleObserver.dispatchAppInitialized()
    }
}
