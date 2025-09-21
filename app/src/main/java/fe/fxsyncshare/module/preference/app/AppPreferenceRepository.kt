package fe.fxsyncshare.module.preference.app

import android.content.Context
import fe.android.preference.helper.compose.StatePreferenceRepository

class AppPreferenceRepository(context: Context) : StatePreferenceRepository(context) {

    init {
        AppPreferences.runMigrations(this)
    }
}
