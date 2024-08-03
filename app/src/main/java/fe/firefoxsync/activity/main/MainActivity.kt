package fe.firefoxsync.activity.main

import android.os.Bundle
import androidx.navigation.compose.rememberNavController
import fe.firefoxsync.activity.BaseComponentActivity
import fe.firefoxsync.theme.BoxAppHost
import kotlinx.coroutines.Job
import mozilla.components.support.base.log.logger.Logger

class MainActivity : BaseComponentActivity() {
    private val logger = Logger("FirefoxSyncShare")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = false) {
            BoxAppHost {
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
