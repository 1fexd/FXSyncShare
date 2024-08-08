package fe.fxsyncshare.module.viewmodel


import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import fe.fxsyncshare.module.preference.app.AppPreferences
import fe.fxsyncshare.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.asState(AppPreferences.theme)

    val themeMaterialYou = preferenceRepository.asState(AppPreferences.themeMaterialYou)
    var themeAmoled = preferenceRepository.asState(AppPreferences.themeAmoled)
}
