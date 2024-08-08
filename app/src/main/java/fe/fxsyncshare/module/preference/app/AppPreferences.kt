package fe.fxsyncshare.module.preference.app


import fe.android.preference.helper.PreferenceDefinition
import fe.fxsyncshare.composable.theme.ThemeType

object AppPreferences : PreferenceDefinition(

) {
    val theme = mapped("theme", ThemeType.System, ThemeType)
    val themeMaterialYou = boolean("theme_material_you", true)
    val themeAmoled = boolean("theme_amoled_enabled")


    init {
        finalize()
    }
}


