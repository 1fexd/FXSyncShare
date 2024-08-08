package fe.fxsyncshare.composable.component.icon

import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color


@Stable
internal fun IconButtonColors.containerColor(enabled: Boolean): Color =
    if (enabled) containerColor else disabledContainerColor

@Stable
internal fun IconButtonColors.contentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor
