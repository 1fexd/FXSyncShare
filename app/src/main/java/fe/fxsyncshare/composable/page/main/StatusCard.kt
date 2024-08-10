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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.PreviewThemeNew
import fe.fxsyncshare.R
import fe.fxsyncshare.Routes
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
    if (syncStatus != SyncStatus.Idle || accountEvent !is AccountEvent.Ready) {
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
//    isDefaultBrowser: Boolean,
    sync: () -> Unit,
//    onSetAsDefault: () -> Unit,
) {
    val isSetup = oauthAccount != null
    val isInitializing = accountEvent !is AccountEvent.Ready
//    account?.getProfile()

    val containerColor = cardContainerColor(isSetup)
    val buttonColor = buttonColor(isSetup)

    val icon = icon(syncStatus, accountEvent, oauthAccount)
    val title = title(syncStatus, accountEvent, oauthAccount)
    val subtitle = subtitle(syncStatus, accountEvent, oauthAccount, profile)

    ClickableAlertCard2(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        contentDescription = null,
        headline = textContent(title),
        subtitle = text(subtitle),
        imageVector = icon,
    ) {
        LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            if (isSetup) {
//                item(key = R.string.settings_main_setup__button_sync_now) {
//                    IconButton(
//                        colors = IconButtonDefaults.iconButtonColors(containerColor = buttonColor),
//                        onClick = {
//
//                        }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Rounded.Sync,
//                            tint = contentColorFor(buttonColor),
//                            contentDescription = stringResource(R.string.settings_main_setup__button_sync_now)
//                        )
//                    }

//                    StatusCardButton(
//                        id = R.string.settings_main_setup__button_sync_now,
//                        buttonColor = buttonColor,
//                        onClick = {
////                            launchIntent(MainViewModel.SettingsIntent.DefaultApps)
//                        }
//                    )
//                }

                item(key = R.string.settings_main_setup_success__button_sync) {
                    StatusCardButton(
                        id = R.string.settings_main_setup_success__button_sync,
                        buttonColor = buttonColor,
                        onClick = sync
                    )
                }
            } else {
                item(key = R.string.settings_main_setup__button_login) {
                    StatusCardButton(
                        id = R.string.settings_main_setup__button_login,
                        buttonColor = buttonColor,
                        onClick = { navigate(Routes.Login) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCardButton(
    @StringRes id: Int,
    buttonColor: Color,
    onClick: () -> Unit,
) {
    Button(
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
//        StatusCard(
//            isDefaultBrowser = true,
//            launchIntent = {},
//            onSetAsDefault = {}
//        )
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
