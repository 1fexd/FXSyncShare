package fe.fxsyncshare.composable.page.main

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.PreviewThemeNew
import fe.fxsyncshare.R
import fe.fxsyncshare.module.fxa.AccountEvent
import fe.fxsyncshare.module.fxa.SyncStatus
import fe.fxsyncshare.module.fxa.isLoggedIn
import fe.fxsyncshare.module.fxa.isNotLoggedIn
import mozilla.components.concept.sync.AccessTokenInfo
import mozilla.components.concept.sync.AttachedClient
import mozilla.components.concept.sync.AuthFlowUrl
import mozilla.components.concept.sync.AuthType
import mozilla.components.concept.sync.Avatar
import mozilla.components.concept.sync.DeviceConstellation
import mozilla.components.concept.sync.FxAEntryPoint
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.concept.sync.Profile
import mozilla.components.concept.sync.StatePersistenceCallback

@Composable
private fun cardContainerColor(isSetup: Boolean): Color {
    return if (isSetup) MaterialTheme.colorScheme.primaryContainer
    else MaterialTheme.colorScheme.errorContainer
}

@Composable
private fun buttonColor(isSetup: Boolean): Color {
    return if (isSetup) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.error
}

private val Profile.name
    get() = displayName ?: email

private fun title(cardState: CardState): Int {
    if (cardState is CardState.Syncing) {
        return R.string.settings_main_setup__title_fxsyncshare_syncing
    }

    return if (cardState is CardState.LoggedIn) R.string.settings_main_setup__title_fxsyncshare_setup_success
    else R.string.settings_main_setup__title_fxsyncshare_setup_failure
}

private fun icon(cardState: CardState): ImageVector? {
    if (cardState is CardState.Syncing) {
        return null
    }

    return if (cardState is CardState.LoggedIn) Icons.Rounded.CheckCircleOutline
    else Icons.Rounded.ErrorOutline
}

@Composable
private fun subtitle(cardState: CardState): String {
    if (cardState is CardState.LoggedIn) {
        return stringResource(
            id = R.string.settings_main_setup__text_fxsyncshare_setup_success_info,
            cardState.profile?.name ?: ""
        )
    }
    if (cardState is CardState.NotLoggedIn) {
        return stringResource(id = R.string.settings_main_setup__text_fxsyncshare_setup_failure_info)
    }

    if (cardState is CardState.Syncing) {
        return stringResource(id = R.string.settings_main_setup__text_fxsyncshare_setup_sync_info)
    }

    return stringResource(id = R.string.settings_main_setup__text_fxsyncshare_setup_failure_info)
}

sealed interface CardState {
    class LoggedIn(val profile: Profile?) : CardState
    class NotLoggedIn : CardState
    class Syncing : CardState
}

private fun toState(
    syncStatus: SyncStatus,
    accountEvent: AccountEvent,
    oauthAccount: OAuthAccount?,
    profile: Profile?
): CardState {
    if (oauthAccount != null) {
        return CardState.LoggedIn(profile)
    }
    if (accountEvent.isNotLoggedIn) {
        return CardState.NotLoggedIn()
    }

    if (syncStatus !is SyncStatus.Idle || !accountEvent.isLoggedIn) {
        return CardState.Syncing()
    }

    return CardState.NotLoggedIn()
}


@Composable
internal fun StatusCard(
    syncStatus: SyncStatus,
    accountEvent: AccountEvent,
    oauthAccount: OAuthAccount?,
    profile: Profile?,
    navigate: (String) -> Unit,
    sync: () -> Unit,
) {
    val isSetup = oauthAccount != null
    val isReady = accountEvent.isLoggedIn
    val isSyncIdle = syncStatus is SyncStatus.Idle

    val containerColor = cardContainerColor(isSetup)
    val buttonColor = buttonColor(isSetup)

    val cardState = remember(syncStatus, accountEvent, oauthAccount) {
        toState(syncStatus, accountEvent, oauthAccount, profile)
    }

    val icon = icon(cardState)
    val title = title(cardState)
    val subtitle = subtitle(cardState)

    val row = rememberOptionalContent(isSetup, isSetup, isReady, isSyncIdle) {
        LaunchedEffect(Unit) {
            Log.d("StatusCard", "isSetup=$isSetup, isReady=$isReady, isSyncIdle=$isSyncIdle")
        }
        LoggedInRow(
            buttonColor = buttonColor,
            syncEnabled = isReady && isSyncIdle,
            sync = sync
        )
    }

    ClickableAlertCard2(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        contentDescription = null,
        headline = textContent(title),
        subtitle = text(subtitle),
        imageVector = icon,
        content = row
    )
}

@Composable
private fun LoggedInRow(
    buttonColor: Color,
    syncEnabled: Boolean,
    sync: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item(key = R.string.settings_main_setup_success__button_sync) {
            StatusCardButton(
                enabled = syncEnabled,
                id = R.string.settings_main_setup_success__button_sync,
                buttonColor = buttonColor,
                onClick = sync
            )
        }
    }
}

@Composable
private fun StatusCardButton(
    @StringRes id: Int,
    enabled: Boolean = true,
    buttonColor: Color,
    onClick: () -> Unit,
) {
    Button(
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        onClick = onClick
    ) {
        Text(text = stringResource(id = id))
    }
}

private class StatusCardPreviewProvider() : PreviewParameterProvider<StatusCardPreviewData> {
    companion object {
        private val Profile = Profile(
            uid = "uid",
            email = "mail",
            avatar = Avatar("https://example.org/avatar", false),
            displayName = "some person"
        )
        private val Account = AccountDummy()
    }

    override val values = sequenceOf(
        StatusCardPreviewData(
            syncStatus = SyncStatus.Idle,
            accountEvent = AccountEvent.Authenticated(
                account = Account,
                authType = AuthType.Signin
            ),
            oAuthAccount = Account,
            profile = Profile
        ),
        StatusCardPreviewData(
            syncStatus = SyncStatus.Idle,
            accountEvent = AccountEvent.Ready(
                authenticatedAccount = Account
            ),
            oAuthAccount = Account,
            profile = Profile
        ),
        StatusCardPreviewData(
            syncStatus = SyncStatus.Idle,
            accountEvent = AccountEvent.LoggedOut,
            oAuthAccount = null,
            profile = null
        )
    )
}

class AccountDummy : OAuthAccount {
    override suspend fun beginOAuthFlow(
        scopes: Set<String>,
        entryPoint: FxAEntryPoint
    ): AuthFlowUrl? {
        TODO("Not yet implemented")
    }

    override suspend fun beginPairingFlow(
        pairingUrl: String,
        scopes: Set<String>,
        entryPoint: FxAEntryPoint
    ): AuthFlowUrl? {
        TODO("Not yet implemented")
    }

    override fun getCurrentDeviceId(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun handleWebChannelLogin(jsonPayload: String) {
        TODO("Not yet implemented")
    }

    override fun getSignedInUserForWebChannel(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getProfile(ignoreCache: Boolean): Profile? {
        TODO("Not yet implemented")
    }

    override suspend fun completeOAuthFlow(
        code: String,
        state: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAccessToken(singleScope: String): AccessTokenInfo? {
        TODO("Not yet implemented")
    }

    override suspend fun getAttachedClient(): List<AttachedClient> {
        TODO("Not yet implemented")
    }

    override fun authErrorDetected() {
        TODO("Not yet implemented")
    }

    override suspend fun checkAuthorizationStatus(singleScope: String): Boolean? {
        TODO("Not yet implemented")
    }

    override suspend fun getTokenServerEndpointURL(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun getManageAccountURL(entryPoint: FxAEntryPoint): String? {
        TODO("Not yet implemented")
    }

    override fun getPairingAuthorityURL(): String {
        TODO("Not yet implemented")
    }

    override fun registerPersistenceCallback(callback: StatePersistenceCallback) {
        TODO("Not yet implemented")
    }

    override fun deviceConstellation(): DeviceConstellation {
        TODO("Not yet implemented")
    }

    override suspend fun disconnect(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toJSONString(): String {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

}

data class StatusCardPreviewData(
    val syncStatus: SyncStatus,
    val accountEvent: AccountEvent,
    val oAuthAccount: OAuthAccount?,
    val profile: Profile?
)

@Composable
@Preview(showBackground = true, apiLevel = 35)
fun StatusCardPreview(@PreviewParameter(StatusCardPreviewProvider::class) state: StatusCardPreviewData) {
    PreviewThemeNew {
        StatusCard(
            syncStatus = state.syncStatus,
            accountEvent = state.accountEvent,
            oauthAccount = state.oAuthAccount,
            profile = state.profile,
            navigate = {

            },
            sync = {

            }
        )
    }
}

@Preview
@Composable
fun StatusCardPreviewNonDefault() {
    PreviewThemeNew {
//        StatusCard(
//            isDefaultBrowser = false,
//            launchIntent = {},
//            onSetAsDefault = {}
//        )
    }
}
