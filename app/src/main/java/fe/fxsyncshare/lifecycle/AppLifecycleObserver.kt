package fe.fxsyncshare.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope

class AppLifecycleObserver(
    private val owner: LifecycleOwner,
) : DefaultLifecycleObserver {
    private val services = mutableListOf<Service>()
    val coroutineScope = owner.lifecycleScope

    fun register(service: Service) {
        services.add(service)
    }

    fun attach() {
        owner.lifecycle.addObserver(this)
    }

    fun dispatchAppInitialized() {
        Log.d("AppLifecycle", "Starting ${services.size} services")
        services.forEach { it.onAppInitialized(owner) }
    }

//    override fun onCreate(owner: LifecycleOwner) {
//        Log.d("AppLifecycle", "Shutting down ${services.size} services")
//        services.forEach { it.boot(owner.lifecycle) }
//    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d("AppLifecycle", "Shutting down ${services.size} services")
        services.forEach { it.onStop(owner) }

        this.owner.lifecycle.removeObserver(this)
    }
}
