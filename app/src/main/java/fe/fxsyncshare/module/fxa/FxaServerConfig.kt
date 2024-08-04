package fe.fxsyncshare.module.fxa

import mozilla.appservices.fxaclient.FxaServer
import mozilla.components.service.fxa.ServerConfig

class FxaServerConfig(
    private val clientId: String,
    fnRedirectUrl: (String) -> String,
    private val allowDomesticChinaFxaServer: Boolean = false,
    private val isMozillaOnline: Boolean = false,
    private val overrideFxAServer: String? = null,
    private val overrideSyncTokenServer: String? = null,
) {
    val redirectUrl = fnRedirectUrl(clientId)

    companion object {
        val DemoReleaseClient = FxaServerConfig("3c49430b43dfba77", { "https://accounts.firefox.com/oauth/success/$it" })
        val FenixReleaseClient = FxaServerConfig("a2270f727f45f648", { "urn:ietf:wg:oauth:2.0:oob:oauth-redirect-webchannel" })
    }

    fun toServerConfig(): ServerConfig {
        // If a server override is configured, use that. Otherwise:
        // - for all channels other than Mozilla Online, use FxaServer.Release.
        // - for Mozilla Online channel, if domestic server is allowed, use FxaServer.China; otherwise,
        //   use FxaServer.Release
        val serverOverride = overrideFxAServer
        val tokenServerOverride = overrideSyncTokenServer
        if (serverOverride.isNullOrEmpty()) {
            val releaseServer = if (isMozillaOnline && allowDomesticChinaFxaServer) {
                FxaServer.China
            } else {
                FxaServer.Release
            }
            return ServerConfig(releaseServer, clientId, redirectUrl, tokenServerOverride)
        }
        return ServerConfig(FxaServer.Custom(serverOverride), clientId, redirectUrl, tokenServerOverride)
    }
}
