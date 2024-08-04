package fe.fxsyncshare.module.viewmodel.module

import fe.fxsyncshare.module.viewmodel.BottomSheetViewModel
import fe.fxsyncshare.module.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::BottomSheetViewModel)
}
