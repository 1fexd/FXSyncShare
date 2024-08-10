package fe.fxsyncshare.composable.page.login

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fe.fxsyncshare.R
import fe.fxsyncshare.Routes
import fe.fxsyncshare.composable.page.main.LoginCallback
import fe.fxsyncshare.module.viewmodel.LoginState
import fe.fxsyncshare.module.viewmodel.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginRoute(navigate: (String) -> Unit, viewModel: LoginViewModel = koinViewModel()) {
    val loginState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = loginState) {
        if (loginState is LoginState.Idle) {
            viewModel.start()
//            val authUrl = viewModel.startLogin()
//            if (authUrl == null) {
//                Toast.makeText(context, R.string.settings_login__load_fail_text, Toast.LENGTH_SHORT).show()
//                return@LaunchedEffect
//            }
        } else if (loginState is LoginState.Finished) {
            val state = loginState as LoginState.Finished

            if (!state.success) {
                Toast.makeText(context, R.string.settings_login__login_fail_text, Toast.LENGTH_SHORT).show()
            }

            navigate(Routes.Main)
        }
    }

    Box(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
    ) {
        when (loginState) {
            is LoginState.Idle -> {
                LoadingIndicator(id = R.string.settings_login__loading_title)
            }

            is LoginState.Login -> {
                LoginWebView(
                    redirectUrl = viewModel.redirectUrl,
                    authUrl = (loginState as LoginState.Login).authUrl,
                    onLoginComplete = viewModel::finish
                )
            }

            is LoginState.Finishing -> {
                LoadingIndicator(id = R.string.settings_login__finishing_title)
            }

            else -> {}
        }
    }
}

@Composable
private fun BoxScope.LoadingIndicator(@StringRes id: Int) {
    Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(id))
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


