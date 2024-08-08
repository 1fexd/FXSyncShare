package fe.fxsyncshare.composable.theme

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.graphics.Color
import fe.android.compose.component.page.GroupValueProvider
import fe.android.preference.helper.OptionTypeMapper
import fe.fxsyncshare.R
import fe.fxsyncshare.util.AndroidVersion
import fe.fxsyncshare.util.StringResHolder


sealed class ThemeType(val name: String, @StringRes stringRes: Int) : StringResHolder, GroupValueProvider<Int> {
    override val id: Int = stringRes
    override val key: Int = stringRes

    abstract fun getColorScheme(
        context: Context,
        systemDarkTheme: Boolean,
        materialYou: Boolean,
        amoled: Boolean,
    ): ColorScheme

    data object System : ThemeType("system", R.string.settings_theme__system) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): ColorScheme {
            val theme = if (systemDarkTheme) Dark else Light
            return theme.getColorScheme(context, systemDarkTheme, materialYou, amoled)
        }
    }

    data object Light : ThemeType("light", R.string.settings_theme__light) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): ColorScheme {
            return if (AndroidVersion.AT_LEAST_API_31_S && materialYou) dynamicLightColorScheme(context) else LightColors
        }
    }

    data object Dark : ThemeType("dark", R.string.settings_theme__dark) {
        override fun getColorScheme(
            context: Context,
            systemDarkTheme: Boolean,
            materialYou: Boolean,
            amoled: Boolean,
        ): ColorScheme {
            val scheme = if (AndroidVersion.AT_LEAST_API_31_S && materialYou) dynamicDarkColorScheme(context) else DarkColors

            val colorScheme = if (amoled) scheme.copy(surface = Color.Black, background = Color.Black)
            else scheme

            return colorScheme
        }
    }

    companion object : OptionTypeMapper<ThemeType, String>({ it.name }, {
        arrayOf(System, Light, Dark)
    })
}
