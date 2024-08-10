package fe.fxsyncshare.module.viewmodel.base

import androidx.lifecycle.ViewModel
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository

abstract class BaseViewModel(
    preferenceRepository: AppPreferenceRepository,
) : ViewModel()
