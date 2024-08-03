package fe.firefoxsync.module.firefox

import kotlinx.coroutines.flow.MutableStateFlow
import mozilla.components.concept.sync.*

class AccountEventObserver(
    private val flow: MutableStateFlow<AccountEvent>,
) : AccountObserver {
    override fun onReady(authenticatedAccount: OAuthAccount?) {
        flow.tryEmit(AccountEvent.Ready(authenticatedAccount))
    }

    override fun onAuthenticationProblems() {
        flow.tryEmit(AccountEvent.AuthenticationProblems)
    }

    override fun onLoggedOut() {
        flow.tryEmit(AccountEvent.LoggedOut)
    }

    override fun onFlowError(error: AuthFlowError) {
        flow.tryEmit(AccountEvent.FlowError(error))
    }

    override fun onProfileUpdated(profile: Profile) {
        flow.tryEmit(AccountEvent.ProfileUpdated(profile))
    }

    override fun onAuthenticated(account: OAuthAccount, authType: AuthType) {
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
