package fe.fxsyncshare.module.preference.app

import android.content.Context
import fe.composekit.preference.FlowPreferenceRepository
import fe.composekit.theme.preference.ThemePreferences

class AppPreferenceRepository(context: Context) : FlowPreferenceRepository(context) {

    init {
        AppPreferences.runMigrations(this)
        ThemePreferences.runMigrations(this)
    }
}
