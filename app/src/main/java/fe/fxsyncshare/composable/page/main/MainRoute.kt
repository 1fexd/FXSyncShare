package fe.fxsyncshare.composable.page.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import fe.android.compose.component.page.layout.SaneLazyColumnPageLayout
import fe.fxsyncshare.R
import fe.fxsyncshare.composable.theme.LocalActivity
import fe.fxsyncshare.module.viewmodel.MainViewModel

import fe.fxsyncshare.composable.theme.HkGroteskFontFamily

import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMainRoute(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    val activity = LocalActivity.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current


    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = {
//                    navController.navigate(settingsRoute)
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
        )
    }) { padding ->
        SaneLazyColumnPageLayout(padding = padding, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.app_name),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                )

//                if (!LinkSheetAppConfig.showDonationBanner()) {
//                    Text(text = stringResource(id = R.string.thanks_for_donating))
//                }
            }

//            if (BuildType.current.allowDebug) {
//                item {
//                    DebugComposable.MainRoute.compose(currentComposer, 0)
//                }
//            }
//
//            if (BuildType.current.allowDebug) {
//                item {
//                    FilledTonalButton(
//                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
//                        onClick = { navController.navigate(Routes.RuleOverview) }
//                    ) {
//                        Text(text = "Rules")
//                    }
//                }
//            }

        }
    }
}
