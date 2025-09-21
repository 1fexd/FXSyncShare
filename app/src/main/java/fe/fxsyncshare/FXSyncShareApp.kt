package fe.fxsyncshare

import android.app.Application
import fe.android.lifecycle.ProcessServiceRegistry
import fe.android.lifecycle.koin.extension.applicationLifecycle
import fe.droidkit.koin.androidApplicationContext
import fe.fxsyncshare.module.fxa.firefoxSyncModule
import fe.fxsyncshare.module.preference.preferenceRepositoryModule
import fe.fxsyncshare.module.viewmodel.module.viewModelModule
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class FXSyncShareApp : Application() {
    private val lifecycleObserver by lazy { ProcessServiceRegistry() }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidApplicationContext<FXSyncShareApp>(this@FXSyncShareApp)
            applicationLifecycle(lifecycleObserver)
            modules(
                preferenceRepositoryModule,
                firefoxSyncModule,
                viewModelModule
            )
        }

        lifecycleObserver.onAppInitialized()
    }
}
