package fe.firefoxsync.lifecycle

import androidx.lifecycle.LifecycleOwner

interface Service {
    fun onAppInitialized(owner: LifecycleOwner) {}

    fun onPause(owner: LifecycleOwner) {}

    fun onStop(owner: LifecycleOwner)
}
