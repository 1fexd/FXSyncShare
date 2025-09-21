package fe.fxsyncshare.composable.page.main

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.content.OptionalContent
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.PreviewThemeNew
import fe.fxsyncshare.R
import fe.fxsyncshare.module.fxa.AccountEvent
import fe.fxsyncshare.module.fxa.SyncStatus
import mozilla.components.concept.sync.OAuthAccount
import mozilla.components.concept.sync.Profile

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

private fun title(syncStatus: SyncStatus, accountEvent: AccountEvent, oauthAccount: OAuthAccount?): Int {
    if (syncStatus != SyncStatus.Idle || accountEvent !is AccountEvent.Ready) {
        return R.string.settings_main_setup__title_fxsyncshare_syncing
    }

    return if (oauthAccount != null) R.string.settings_main_setup__title_fxsyncshare_setup_success
    else R.string.settings_main_setup__title_fxsyncshare_setup_failure
}

private fun icon(syncStatus: SyncStatus, accountEvent: AccountEvent, oauthAccount: OAuthAccount?): ImageVector? {
    if (syncStatus != SyncStatus.Idle || accountEvent !is AccountEvent.Ready) {
        return null
    }

    return if (oauthAccount != null) Icons.Rounded.CheckCircleOutline
    else Icons.Rounded.ErrorOutline
}

@Composable
private fun subtitle(
    syncStatus: SyncStatus,
    accountEvent: AccountEvent,
    oauthAccount: OAuthAccount?,
    profile: Profile?,
): String {
    if (syncStatus !is SyncStatus.Idle || accountEvent !is AccountEvent.Ready) {
        return stringResource(id = R.string.settings_main_setup__text_fxsyncshare_setup_sync_info)
    }

    return if (oauthAccount != null) stringResource(
        id = R.string.settings_main_setup__text_fxsyncshare_setup_success_info,
        profile?.name ?: ""
    )
    else stringResource(id = R.string.settings_main_setup__text_fxsyncshare_setup_failure_info)
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
    val isReady = accountEvent is AccountEvent.Ready
    val isSyncIdle = syncStatus is SyncStatus.Idle

    val containerColor = cardContainerColor(isSetup)
    val buttonColor = buttonColor(isSetup)

    val icon = icon(syncStatus, accountEvent, oauthAccount)
    val title = title(syncStatus, accountEvent, oauthAccount)
    val subtitle = subtitle(syncStatus, accountEvent, oauthAccount, profile)

    val row = rememberOptionalContent(isSetup) {
        LoggedInRow(
            buttonColor = buttonColor,
            isSetup = isSetup,
            isReady = isReady,
            isSyncIdle = isSyncIdle,
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
private fun LoggedInRow(buttonColor: Color, isSetup: Boolean, isReady: Boolean, isSyncIdle: Boolean, sync: () -> Unit) {
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        if (isSetup) {
            item(key = R.string.settings_main_setup_success__button_sync) {
                StatusCardButton(
                    enabled = isReady && isSyncIdle,
                    id = R.string.settings_main_setup_success__button_sync,
                    buttonColor = buttonColor,
                    onClick = sync
                )
            }
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

@Preview
@Composable
fun StatusCardPreview() {
    PreviewThemeNew {
        StatusCard(
            syncStatus = SyncStatus.Started,
            accountEvent = AccountEvent.Waiting,
            oauthAccount = null,
            profile = null,
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
