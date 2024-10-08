package fe.fxsyncshare.module.preference.app

import android.content.Context
import fe.android.preference.helper.compose.StatePreferenceRepository
import org.koin.core.component.KoinComponent

class AppPreferenceRepository(context: Context) : StatePreferenceRepository(context), KoinComponent {

    init {
        AppPreferences.runMigrations(this)
    }
}
