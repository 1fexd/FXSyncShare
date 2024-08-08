package fe.fxsyncshare.composable.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import fe.android.compose.component.util.ComposeClipboardManager
import fe.android.compose.component.util.LocalComposeClipboardManager
import fe.android.span.helper.LinkAnnotationStyle
import fe.android.span.helper.LocalLinkAnnotationStyle
import fe.fxsyncshare.activity.BaseComponentActivity
import fe.fxsyncshare.composable.DefaultHapticFeedbackInteraction
import fe.fxsyncshare.composable.LocalHapticFeedbackInteraction
import fe.fxsyncshare.module.viewmodel.ThemeSettingsViewModel

import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

val LocalActivity = staticCompositionLocalOf<Activity> { error("CompositionLocal LocalActivity not present") }


/**
 * The default light scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=35-38;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

/**
 * The default dark scrim, as defined by androidx and the platform:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:activity/activity/src/main/java/androidx/activity/EdgeToEdge.kt;l=40-44;drc=27e7d52e8604a080133e8b842db10c89b4482598
 */
private val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@Composable
fun BaseComponentActivity.AppTheme(
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    themeSettingsViewModel: ThemeSettingsViewModel = koinViewModel(),
    content: @Composable () -> Unit,
) {
    AppTheme(
        edgeToEdge = edgeToEdge,
        systemDarkTheme = systemDarkTheme,
        themeSettingsViewModel = themeSettingsViewModel,
        updateEdgeToEdge = { status, nav -> enableEdgeToEdge(statusBarStyle = status, navigationBarStyle = nav) },
        content = content
    )
}

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AppTheme(
    edgeToEdge: Boolean = true,
    systemDarkTheme: Boolean = isSystemInDarkTheme(),
    themeSettingsViewModel: ThemeSettingsViewModel = koinViewModel(),
    updateEdgeToEdge: ((SystemBarStyle, SystemBarStyle) -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val theme = themeSettingsViewModel.theme()

    val colorScheme = theme.getColorScheme(
        context,
        systemDarkTheme,
        themeSettingsViewModel.themeMaterialYou(),
        themeSettingsViewModel.themeAmoled()
    )

    val activity = context.findActivity()

    if (edgeToEdge && updateEdgeToEdge != null) {
        LaunchedEffect(key1 = theme) {
            val isDarkMode: (Resources) -> Boolean = { _ -> theme == ThemeType.Dark || systemDarkTheme }

            updateEdgeToEdge(
                SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT, detectDarkMode = isDarkMode),
                SystemBarStyle.auto(lightScrim, darkScrim, detectDarkMode = isDarkMode)
            )
        }
    }

    val view = LocalView.current

    val linkAnnotationStyle = remember(colorScheme) {
        LinkAnnotationStyle(style = SpanStyle(color = colorScheme.primary))
    }

    KoinAndroidContext {
        CompositionLocalProvider(
            LocalActivity provides activity!!,
            LocalComposeClipboardManager provides ComposeClipboardManager(
                context
            ),
            LocalHapticFeedbackInteraction provides DefaultHapticFeedbackInteraction(view),
            LocalLinkAnnotationStyle provides linkAnnotationStyle
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = NewTypography,
                content = content
            )
        }
    }
}

@Composable
fun BaseComponentActivity.BoxAppHost(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    AppTheme {
        Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
    }
}
