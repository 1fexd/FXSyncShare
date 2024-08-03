package fe.firefoxsync.module.viewmodel.module

import fe.firefoxsync.module.viewmodel.BottomSheetViewModel
import fe.firefoxsync.module.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::BottomSheetViewModel)
}
