package fe.firefoxsync.composable.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import fe.firefoxsync.composable.main.LoginCallback
import fe.firefoxsync.composable.main.LoginWebViewClient
import fe.firefoxsync.module.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.sync.DeviceCapability
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainRoute(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    val scope = rememberCoroutineScope()

    var authUrl by rememberSaveable { mutableStateOf<String?>(null) }

    val accountEvent by viewModel.fxaService.accountEvents.collectAsStateWithLifecycle(context = Dispatchers.Main)
    val constellationState by viewModel.deviceConstellationState.collectAsStateWithLifecycle(context = Dispatchers.Main)

    var hasRegisteredObservers by remember { mutableStateOf(false) }

    val owner = LocalLifecycleOwner.current
    LaunchedEffect(key1 = accountEvent) {
        Log.d("MainRoute", "accountEvent: $accountEvent")

        if (constellationState != null || hasRegisteredObservers) return@LaunchedEffect
        viewModel.fxaService.accountManager.authenticatedAccount()?.let { account ->
            viewModel.registerDeviceObserver(account, owner)
            hasRegisteredObservers = true
        }
    }

    val targets = remember(constellationState) {
        constellationState?.otherDevices?.filter { it.capabilities.contains(DeviceCapability.SEND_TAB) }
    }

    if (authUrl == null) {
        Column {
            Text(text = accountEvent.toString())

            Button(onClick = {
                scope.launch { authUrl = viewModel.startLogin() }
            }) {
                Text(text = "Login")
            }

            Button(onClick = {
                scope.launch { viewModel.refreshDevices() }
            }) {
                Text(text = "Refresh devices")
            }

            targets?.forEach {
                Text(text = it.toString())
            }
        }
    } else {
        LoginWebView(redirectUrl = viewModel.redirectUrl, authUrl = authUrl!!) { code, state, action ->
            scope.launch {
                viewModel.finishLogin(code, state, action)
                authUrl = null
            }
        }
    }
}


@Composable
private fun LoginWebView(redirectUrl: String, authUrl: String, onLoginComplete: LoginCallback) {
    AndroidView(
        factory = { it.createWebView(redirectUrl, onLoginComplete = onLoginComplete) },
        update = { it.loadUrl(authUrl) }
    )
}

private fun Context.createWebView(redirectUrl: String, onLoginComplete: LoginCallback): WebView {
    val webView = WebView(this)

    webView.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    // Need JS, cookies and localStorage.
    webView.settings.domStorageEnabled = true
    @SuppressLint("SetJavaScriptEnabled")
    webView.settings.javaScriptEnabled = true

    CookieManager.getInstance().setAcceptCookie(true)

    webView.webViewClient = LoginWebViewClient(redirectUrl, onLoginComplete)
    return webView
}


