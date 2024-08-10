package fe.fxsyncshare.module.viewmodel


import fe.composekit.theme.preference.ThemePreferences
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import fe.fxsyncshare.module.viewmodel.base.BaseViewModel

class ThemeSettingsViewModel(
    val preferenceRepository: AppPreferenceRepository,
) : BaseViewModel(preferenceRepository) {
    var theme = preferenceRepository.asState(ThemePreferences.theme)
    val themeMaterialYou = preferenceRepository.asState(ThemePreferences.themeMaterialYou)
    var themeAmoled = preferenceRepository.asState(ThemePreferences.themeAmoled)
}
