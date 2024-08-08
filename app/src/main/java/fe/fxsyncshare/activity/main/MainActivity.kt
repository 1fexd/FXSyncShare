package fe.fxsyncshare.activity.main

import android.os.Bundle
import androidx.navigation.compose.rememberNavController
import fe.fxsyncshare.activity.BaseComponentActivity
import fe.fxsyncshare.composable.theme.AppTheme
import fe.fxsyncshare.composable.theme.BoxAppHost
import mozilla.components.support.base.log.logger.Logger

class MainActivity : BaseComponentActivity() {
    private val logger = Logger("FirefoxSyncShare")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = false) {
            AppTheme {
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
