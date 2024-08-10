package fe.fxsyncshare.module.fxa

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mozilla.components.service.fxa.sync.SyncStatusObserver
import mozilla.components.support.base.log.logger.Logger


abstract class ObserverStateFlow<T>(value: T) {
    private val logger = Logger("AccountSyncObserver")

    private val _state = MutableStateFlow(value)
    val state = _state.asStateFlow()

    fun update(value: T) {
        logger.info("update: $value")
        _state.value = value
    }
}

class AccountSyncObserver : ObserverStateFlow<SyncStatus>(SyncStatus.Idle), SyncStatusObserver {
    override fun onError(error: Exception?) {
        update(SyncStatus.Error(error))
    }

    override fun onIdle() {
        update(SyncStatus.Idle)
    }

    override fun onStarted() {
        update(SyncStatus.Started)
    }
}


sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Started : SyncStatus
    data class Error(val error: Exception?) : SyncStatus
}

