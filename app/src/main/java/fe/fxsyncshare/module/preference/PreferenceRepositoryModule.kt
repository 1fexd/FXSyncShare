package fe.fxsyncshare.module.preference


import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferenceRepositoryModule = module {
    singleOf(::AppPreferenceRepository)
}
