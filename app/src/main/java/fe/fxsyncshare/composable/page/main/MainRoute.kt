package fe.fxsyncshare.composable.page.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.composekit.appbase.LocalActivity
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.fxsyncshare.R
import fe.fxsyncshare.Routes
import fe.fxsyncshare.composable.theme.HkGroteskFontFamily
import fe.fxsyncshare.extension.compose.dashedBorder
import fe.fxsyncshare.module.preference.app.AppPreferences
import fe.fxsyncshare.module.viewmodel.MainViewModel
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
        SaneLazyColumnLayout(padding = padding, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.app_name),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                )
            }

            item(key = R.string.main_page__fxa_status_headline, contentType = ContentType.ClickableAlert) {
                StatusCard(
                    syncStatus = syncStatus,
                    accountEvent = accountEvent, oauthAccount = oauthAccount, profile = profile, navigate = navigate,
                    sync = { scope.launch { viewModel.syncNow(SyncReason.User) } }
                )
            }

            item {
                Button(onClick = { navigate(Routes.Login) }) {
                    Text(text = "Login")
                }
            }

            item {
                Button(onClick = {
                    scope.launch { viewModel.fxaService.accountManager.logout() }
                }) {
                    Text(text = "Logout")
                }
            }

            item {
                Button(onClick = { scope.launch { viewModel.syncNow(SyncReason.User) } }) {
                    Text(text = "Sync now")
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .dashedBorder(1.dp, Color.Gray, 12.dp)
                        .padding(all = 2.dp)
                ) {
                    Text(text = "accountEvent=$accountEvent")
                    Text(text = "syncStatus=$syncStatus")
                    Text(text = "oauthAccount=$oauthAccount")
                    Text(text = "profile=$profile")
                }
            }
        }
    }
}
