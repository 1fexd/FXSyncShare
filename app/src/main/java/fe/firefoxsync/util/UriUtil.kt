package fe.firefoxsync.util

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.webkit.URLUtil

object UriUtil {
    const val HTTP = "http"

    private val protocols = setOf(HTTP, "https")
    private val webSchemeIntents: Set<Intent> = protocols.map {
        Intent(Intent.ACTION_VIEW, Uri.fromParts(it, "", "")).addCategory(Intent.CATEGORY_BROWSABLE)
    }.toSet()

    fun parseWebUriStrict(url: String): Uri? {
        if (!isWebStrict(url)) return null

        return if (Patterns.WEB_URL.matcher(url).matches()) {
            runCatching { Uri.parse(url) }.getOrNull()
        } else null
    }

    fun isWebStrict(url: String, allowInsecure: Boolean = true): Boolean {
        return (URLUtil.isHttpUrl(url) && allowInsecure) || URLUtil.isHttpsUrl(url)
    }
}
