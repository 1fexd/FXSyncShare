package fe.fxsyncshare.composable.page.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.android.compose.component.ContentTypeDefaults
import fe.android.compose.component.page.layout.SaneLazyColumnPageLayout
import fe.fxsyncshare.R
import fe.fxsyncshare.Routes
import fe.fxsyncshare.composable.theme.HkGroteskFontFamily
import fe.fxsyncshare.composable.theme.LocalActivity
import fe.fxsyncshare.extension.compose.dashedBorder
import fe.fxsyncshare.module.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.service.fxa.sync.SyncReason
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMainRoute(navigate: (String) -> Unit, viewModel: MainViewModel = koinViewModel()) {
    val syncStatus by viewModel.fxaService.syncStatus.collectAsStateWithLifecycle()
    val oauthAccount by viewModel.fxaService.oauthAccount.collectAsStateWithLifecycle()
    val profile by viewModel.fxaService.profile.collectAsStateWithLifecycle()


    val accountEvent by viewModel.fxaService.accountEvents.collectAsStateWithLifecycle()
    val constellationState by viewModel.deviceConstellationState.collectAsStateWithLifecycle()

//    var hasRegisteredObservers by remember { mutableStateOf(false) }
//    var account by remember { mutableStateOf<OAuthAccount?>(null) }
//    var profile by remember { mutableStateOf<Profile?>(null) }

    val owner = LocalLifecycleOwner.current
//    LaunchedEffect(key1 = accountEvent) {
//        Log.d("MainRoute", "accountEvent: $accountEvent")
//
//        if (constellationState != null) return@LaunchedEffect
//        viewModel.fxaService.accountManager.authenticatedAccount()?.let {
//            viewModel.registerDeviceObserver(it, owner)
//            account = it
//            profile = it.getProfile()
//        }
//    }

//    LaunchedEffect(key1 = Unit) {
////        viewModel.register(owner)
//        viewModel.syncNow()
//    }

    val activity = LocalActivity.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    val scope = rememberCoroutineScope()

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

            item(key = R.string.main_page__fxa_status_headline, contentType = ContentTypeDefaults.ClickableAlert) {
//                FXAStatusCard(accountEvent = accountEvent, account = account, navigate = {
////                    navController.navigate(it)
//                })

                StatusCard(
                    syncStatus = syncStatus,
                    accountEvent = accountEvent, oauthAccount = oauthAccount, profile = profile, navigate = navigate,
                    sync = { scope.launch { viewModel.syncNow(SyncReason.User) } }
                )
            }

            item {
                Button(onClick = {
                    navigate(Routes.Login)
//                    scope.launch {

//                        navigate(Routes.Login.buildNavigation(LoginRouteData("")))
//                    }
                }) {
                    Text(text = "Login")
                }
            }

            item {
                Button(onClick = {
//                    navigate(Routes.Login)
                    scope.launch {
                        viewModel.fxaService.accountManager.logout()
//                        navigate(Routes.Login.buildNavigation(LoginRouteData("")))
                    }
                }) {
                    Text(text = "Logout")
                }
            }

            item {
                Button(onClick = {
                    scope.launch { viewModel.syncNow(SyncReason.User) }
                }) {
                    Text(text = "Sync now")
                }
            }

            item {
                Column(modifier = Modifier
                    .dashedBorder(1.dp, Color.Gray, 12.dp)
                    .padding(all = 2.dp)) {
                    Text(text = "accountEvent=$accountEvent")
                    Text(text = "syncStatus=$syncStatus")
                    Text(text = "oauthAccount=$oauthAccount")
                    Text(text = "profile=$profile")
                }
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
