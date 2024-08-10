package fe.fxsyncshare.module.fxa

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mozilla.components.concept.sync.*
import mozilla.components.support.base.log.logger.Logger

class AccountEventObserver(
    private val flow: MutableStateFlow<AccountEvent>,
) : AccountObserver {
    private val logger = Logger("AccountEventObserver")

    private val _oauthAccount = MutableStateFlow<OAuthAccount?>(null)
    val oauthAccount = _oauthAccount.asStateFlow()

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile = _profile.asStateFlow()

    override fun onReady(authenticatedAccount: OAuthAccount?) {
        logger.info("onReady: $authenticatedAccount")
        flow.tryEmit(AccountEvent.Ready(authenticatedAccount))

        _oauthAccount.tryEmit(authenticatedAccount)
    }

    override fun onAuthenticationProblems() {
        logger.info("onAuthenticationProblems")
        flow.tryEmit(AccountEvent.AuthenticationProblems)
    }

    override fun onLoggedOut() {
        logger.info("onLoggedOut")
        flow.tryEmit(AccountEvent.LoggedOut)

        _oauthAccount.tryEmit(null)
        _profile.tryEmit(null)
    }

    override fun onFlowError(error: AuthFlowError) {
        logger.info("onFlowError: $error")
        flow.tryEmit(AccountEvent.FlowError(error))
    }

    override fun onProfileUpdated(profile: Profile) {
        logger.info("onProfileUpdated: $profile")
        flow.tryEmit(AccountEvent.ProfileUpdated(profile))

        _profile.tryEmit(profile)
    }

    override fun onAuthenticated(account: OAuthAccount, authType: AuthType) {
        logger.info("onAuthenticated: $account, $authType")
        flow.tryEmit(AccountEvent.Authenticated(account, authType))
    }
}

sealed interface AccountEvent {
    data object Waiting : AccountEvent
    data class Ready(val authenticatedAccount: OAuthAccount?) : AccountEvent
    data class FlowError(val error: AuthFlowError) : AccountEvent
    data class ProfileUpdated(val profile: Profile) : AccountEvent
    data object LoggedOut : AccountEvent
    data object AuthenticationProblems : AccountEvent
    data class Authenticated(val account: OAuthAccount, val authType: AuthType) : AccountEvent
}
