package fe.fxsyncshare.composable.page.login

import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient

typealias LoginCallback = (code: String, state: String, action: String) -> Unit

class LoginWebViewClient(
    private val redirectUrl: String,
    val onLoginComplete: LoginCallback,
) : WebViewClient() {
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (url != null && url.startsWith(redirectUrl)) {
            val uri = Uri.parse(url)

            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            val action = uri.getQueryParameter("action") ?: "signin"

            if (code != null && state != null) {
                onLoginComplete(code, state, action)
            }
        }

        super.onPageStarted(view, url, favicon)
    }
}
