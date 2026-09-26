package fe.fxsyncshare.composable.page.main

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.fxsyncshare.BuildConfig
import fe.fxsyncshare.R
import fe.fxsyncshare.Routes
import fe.fxsyncshare.composable.theme.HkGroteskFontFamily
import fe.fxsyncshare.extension.compose.dashedBorder
import fe.fxsyncshare.module.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import mozilla.components.service.fxa.sync.SyncReason
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NewMainRoute(navigate: (String) -> Unit, viewModel: MainViewModel = koinActivityViewModel()) {
    val syncStatus by viewModel.fxaService.syncStatus.collectAsStateWithLifecycle()
    val oauthAccount by viewModel.fxaService.oauthAccount.collectAsStateWithLifecycle()
    val profile by viewModel.fxaService.profile.collectAsStateWithLifecycle()

    val accountEvent by viewModel.fxaService.accountEvents.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val contextActionPermission = rememberPermissionState(permission = "fe.linksheet.permission.CONTEXT_ACTION") {

    }

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
                StatusCard(syncStatus = syncStatus, accountEvent = accountEvent, oauthAccount = oauthAccount, profile = profile, navigate = navigate,
                    sync = { scope.launch { viewModel.syncNow(SyncReason.User) } }
                )
            }

            item(key = "login", contentType = ContentType.Button) {
                Button(onClick = { navigate(Routes.Login) }) {
                    Text(text = "Login")
                }
            }

            item(key = "logout", contentType = ContentType.Button) {
                Button(onClick = {
                    scope.launch { viewModel.fxaService.accountManager.logout() }
                }) {
                    Text(text = "Logout")
                }
            }

            item(key = "sync-now", contentType = ContentType.Button) {
                Button(onClick = { scope.launch { viewModel.syncNow(SyncReason.User) } }) {
                    Text(text = "Sync now")
                }
            }

            item(key = "request-permission", contentType = ContentType.Button) {
                Button(onClick = { contextActionPermission.launchPermissionRequest() }) {
                    Text(text = "Request permission")
                }
            }

            item(key = "push-dynamic-shortcuts", contentType = ContentType.Button) {
                Button(onClick = {
                    val result = viewModel.publishShortcuts()
                    Log.d("MainRoute", "publishShortcuts=$result")
                }) {
                    Text(text = "Push dynamic shortcuts")
                }
            }

            item(key = "toggle-theme", contentType = ContentType.Button) {
                FilledTonalButton(onClick = {
                    viewModel.toggleTheme()
                }) {
                    Text(text = "Toggle theme")
                }
            }
            item(key = "state", contentType = ContentType.Custom) {
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

            if (BuildConfig.DEBUG) {
                item(key = "random-link", contentType = ContentType.Custom) {
                    RandomLinkCard()
                }
            }
        }
    }
}
