package fe.fxsyncshare.activity.main


import android.os.Bundle
import androidx.navigation.compose.rememberNavController
import fe.composekit.appbase.AppBaseComponentActivity
import fe.composekit.appbase.AppTheme
import fe.composekit.theme.preference.PreferenceTheme2
import fe.fxsyncshare.composable.theme.AppColor
import fe.fxsyncshare.composable.theme.Typography
import fe.fxsyncshare.module.viewmodel.MainViewModel
import mozilla.components.support.base.log.logger.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppBaseComponentActivity() {
    private val mainViewModel by viewModel<MainViewModel>()
    private val logger = Logger("FirefoxSyncShare")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent(edgeToEdge = true) {
            PreferenceTheme2(themeHolder = mainViewModel) { theme, themeMaterialYou, themeAmoled ->
                AppTheme(
                    appColor = AppColor,
                    typography = Typography,
                    theme = theme,
                    materialYou = themeMaterialYou,
                    amoled = themeAmoled
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
