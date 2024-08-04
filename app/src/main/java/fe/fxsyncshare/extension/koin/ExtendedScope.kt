package fe.fxsyncshare.extension.koin

import fe.fxsyncshare.FXSyncShareApp
import fe.fxsyncshare.lifecycle.AppLifecycleObserver
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

class ExtendedScope<T : Any>(val scope: Scope, private val clazz: KClass<T>) {
//    val logger by scope.inject<Logger>(parameters = { parametersOf(clazz) })
    val applicationContext by scope.inject<FXSyncShareApp>()
    val applicationLifecycle by scope.inject<AppLifecycleObserver>()
}
