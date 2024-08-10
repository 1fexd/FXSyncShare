package fe.fxsyncshare.composable.page.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object IconDefaults {
    val IconSize = 24.0.dp
    val ContainerSize = 40.0.dp
}

@Composable
fun CircularLoaderIcon(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    iconSize: Dp = IconDefaults.IconSize,
    containerSize: Dp = IconDefaults.ContainerSize,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
) {
    Surface(
        shape = shape,
        color = colors.containerColor(enabled),
        contentColor = colors.contentColor(enabled),
    ) {
        Box(modifier = Modifier.size(containerSize), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(iconSize)
                    .then(modifier),
                strokeWidth = 3.dp
            )
        }
    }
}

@Stable
private fun IconButtonColors.containerColor(enabled: Boolean): Color =
    if (enabled) containerColor else disabledContainerColor

@Stable
internal fun IconButtonColors.contentColor(enabled: Boolean): Color =
    if (enabled) contentColor else disabledContentColor
