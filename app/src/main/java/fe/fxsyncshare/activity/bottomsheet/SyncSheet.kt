@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package fe.fxsyncshare.activity.bottomsheet

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.android.compose.icon.iconPainter
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.icon.FilledIcon
import fe.composekit.component.icon.IconDefaults
import fe.fxsyncshare.R
import fe.fxsyncshare.composable.component.icon.containerColor
import fe.fxsyncshare.composable.component.icon.contentColor
import kotlinx.coroutines.launch
import mozilla.components.concept.sync.Device
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceType

@Composable
fun BottomSheetContent(targets: List<Device>?, closeDrawer: () -> Unit, sendTab: suspend (Device) -> Unit) {
    SheetContainer {
        if (targets != null) {
            SyncDeviceRow(
                targets = targets,
                sendTab = sendTab,
                closeDrawer = closeDrawer
            )
        } else {
            ContainedLoadingIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun SheetContainer(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .heightIn(min = 56.dp)
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp, bottom = 10.dp), content = content
    )
}

@Composable
fun SyncDeviceRow(targets: List<Device>, sendTab: suspend (Device) -> Unit, closeDrawer: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        items(items = targets, key = { it.id }) { device ->
            var loading by remember { mutableStateOf(false) }

            SyncDevice(
                loading = loading,
                device = device,
                onClick = {
                    loading = true
                    coroutineScope.launch { sendTab(device) }.invokeOnCompletion {
                        loading = false
                        Toast.makeText(context, R.string.link_sent, Toast.LENGTH_SHORT).show()
                        closeDrawer()
                    }
                }
            )
        }
    }
}

private val DeviceType.iconPainter
    get() = when (this) {
        DeviceType.DESKTOP -> Icons.Outlined.Computer
        DeviceType.MOBILE -> Icons.Default.Smartphone
        DeviceType.TABLET -> Icons.Default.TabletAndroid
        else -> Icons.Default.DeviceUnknown
    }.iconPainter

@Composable
fun SyncDevice(modifier: Modifier = Modifier, loading: Boolean, device: Device, onClick: () -> Unit) {
    TextButton(modifier = modifier, onClick = onClick) {
        if (loading) {
            SyncDeviceLoadIndicator()
        } else {

//            containerColor: Color = LoadingIndicatorDefaults.containedContainerColor,
//            indicatorColor: Color = LoadingIndicatorDefaults.containedIndicatorColor,

            FilledIcon(
                icon = device.deviceType.iconPainter,
                contentDescription = null,
                colors = IconButtonDefaults.filledIconButtonColors().copy(
                    containerColor = LoadingIndicatorDefaults.containedContainerColor,
                    contentColor = LoadingIndicatorDefaults.containedIndicatorColor
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = device.displayName, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SyncDeviceLoadIndicator(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    iconSize: Dp = IconDefaults.IconSize,
    containerSize: Dp = IconDefaults.ContainerSize,
    shape: Shape = IconButtonDefaults.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
) {
    val expressive = true
    if (expressive) {
        if (LocalInspectionMode.current) {
            ContainedLoadingIndicator(
                modifier = Modifier
                    .size(containerSize)
                    .then(modifier),
                progress = { 0.1f }
            )
//            CircularWavyProgressIndicator()
//            LoadingIndicator(
//                progress = { 0.7f },
//            )
        } else {
            ContainedLoadingIndicator(
                modifier = Modifier
                    .size(containerSize)
                    .then(modifier),
            )
//            LoadingIndicator()
        }
    } else {
        Surface(
            shape = shape,
            color = colors.containerColor(enabled),
            contentColor = colors.contentColor(enabled),
        ) {
            Box(modifier = Modifier.size(containerSize), contentAlignment = Alignment.Center) {
                if (LocalInspectionMode.current) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(iconSize)
                            .then(modifier),
                        color = colors.contentColor,
                        progress = { 0.7f },
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(iconSize)
                            .then(modifier),
                        color = colors.contentColor
                    )
                }
            }
        }
    }
}

private class DevicePreviewProvider() : PreviewParameterProvider<Device> {
    companion object {
        val Desktop = Device(
            "d4f9ad54-bb69-4749-9da4-9817f6361919",
            "Firefox",
            DeviceType.DESKTOP,
            false,
            System.currentTimeMillis(),
            listOf(DeviceCapability.SEND_TAB),
            false,
            null
        )
        val Mobile = Device(
            "c613492b-37be-49f6-a107-c5f33c7b6ee5",
            "FirefoxSync on Android 14",
            DeviceType.MOBILE,
            false,
            System.currentTimeMillis(),
            listOf(DeviceCapability.SEND_TAB),
            false,
            null
        )
    }

    override val values = sequenceOf(Desktop, Mobile)
}


@Composable
@Preview(showBackground = true, apiLevel = 35)
private fun SyncDevicePreview(@PreviewParameter(DevicePreviewProvider::class) state: Device) {
    PreviewThemeNew {
        SyncDeviceRow(targets = listOf(state), sendTab = {}, closeDrawer = {})
    }
}

@Composable
@Preview(showBackground = true, apiLevel = 35)
private fun SyncDevicePreviewLoading() {
    PreviewThemeNew {
        SyncDevice(
            loading = true,
            device = DevicePreviewProvider.Mobile,
            onClick = {

            }
        )
    }
}
