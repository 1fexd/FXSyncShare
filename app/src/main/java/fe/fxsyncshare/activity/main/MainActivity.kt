package fe.fxsyncshare.activity.main


import android.os.Bundle
import androidx.navigation.compose.rememberNavController
import fe.composekit.appbase.AppBaseComponentActivity
import fe.composekit.appbase.AppTheme
import fe.composekit.theme.preference.PreferenceTheme
import fe.fxsyncshare.composable.theme.AppColor
import fe.fxsyncshare.composable.theme.Typography
import fe.fxsyncshare.module.preference.app.AppPreferenceRepository
import fe.fxsyncshare.module.preference.app.AppPreferences
import mozilla.components.support.base.log.logger.Logger
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject

class MainActivity : AppBaseComponentActivity() {
    private val preferences by inject<AppPreferenceRepository>()
    private val logger = Logger("FirefoxSyncShare")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            PreferenceTheme(preferences = preferences) { config ->
                AppTheme(
                    appColor = AppColor,
                    typography = Typography,
                    config = config,
                ) {
                    val navController = rememberNavController()

                    MainNavHost(
                        navController = navController,
                        navigate = { navController.navigate(it) },
                        onBackPressed = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
