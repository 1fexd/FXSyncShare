package fe.fxsyncshare.activity.bottomsheet

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import fe.android.compose.icon.iconPainter
import fe.composekit.appbase.AppBaseComponentActivity
import fe.composekit.appbase.AppTheme
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.icon.FilledIcon
import fe.composekit.component.icon.IconDefaults
import fe.composekit.theme.preference.PreferenceTheme
import fe.fxsyncshare.R
import fe.fxsyncshare.composable.component.bottomsheet.ImprovedBottomDrawer
import fe.fxsyncshare.composable.component.icon.containerColor
import fe.fxsyncshare.composable.component.icon.contentColor
import fe.fxsyncshare.composable.theme.AppColor
import fe.fxsyncshare.composable.theme.Typography
import fe.fxsyncshare.module.viewmodel.BottomSheetViewModel
import fe.fxsyncshare.util.IntentParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.concept.sync.Device
import mozilla.components.concept.sync.DeviceCapability
import mozilla.components.concept.sync.DeviceCommandOutgoing
import mozilla.components.concept.sync.DeviceType
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel

class BottomSheetActivity : AppBaseComponentActivity() {
    private val viewModel by viewModel<BottomSheetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("Intent", "$intent")
        val safeIntent = intent.toSafeIntent()
        val uri = IntentParser.parseSendAction(safeIntent)
        val url = uri.toString()

        lifecycleScope.launch {
            viewModel.fetchDeviceConstellation()?.let { constellation ->
                constellation.registerDeviceObserver(viewModel, this@BottomSheetActivity, autoPause = true)
                val success = constellation.refreshDevices()
                Log.d("Constellation", "Refresh devices success=$success")
            }
        }

        setContent(edgeToEdge = true) {
            AppTheme(
                appColor = AppColor,
                typography = Typography,
                theme = viewModel.theme(),
                materialYou = viewModel.themeMaterialYou(),
                amoled = viewModel.themeAmoled()
            ) { Wrapper(url) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Wrapper(url: String) {
        val constellationState by viewModel.deviceConstellationFlow.collectAsStateWithLifecycle(context = Dispatchers.Main)
        val targets = remember(constellationState) {
            constellationState?.otherDevices?.filter { it.capabilities.contains(DeviceCapability.SEND_TAB) }
        }

        val drawerState = rememberModalBottomSheetState()
        val coroutineScope = rememberCoroutineScope()

        val hideDrawer: () -> Unit = {
            coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
        }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val isBlackTheme = viewModel.themeAmoled()

        ImprovedBottomDrawer(
            landscape = landscape,
            isBlackTheme = isBlackTheme,
            drawerState = drawerState,
            shape = RoundedCornerShape(
                topStart = 22.0.dp,
                topEnd = 22.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = hideDrawer,
            sheetContent = {
                SheetContainer {
                    if (targets != null) {
                        val command = DeviceCommandOutgoing.SendTab("", url)

                        SyncDeviceRow(
                            targets = targets,
                            sendTab = { viewModel.sendTab(it, command) },
                            closeDrawer = hideDrawer
                        )
                    } else {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        )
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
                            Toast.makeText(this@BottomSheetActivity, R.string.link_sent, Toast.LENGTH_SHORT).show()
                            closeDrawer()
                        }
                    }
                )
            }
        }
    }

    private val DeviceType.iconPainter
        get() = when (this) {
            DeviceType.DESKTOP -> Icons.Default.Computer
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
                FilledIcon(icon = device.deviceType.iconPainter, contentDescription = null)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(text = device.displayName, textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun SyncDeviceLoadIndicator(
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
                    color = colors.contentColor
                )
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun SyncDevicePreview() {
        val targets = listOf(
            Device(
                "d4f9ad54-bb69-4749-9da4-9817f6361919",
                "Firefox",
                DeviceType.DESKTOP,
                false,
                System.currentTimeMillis(),
                listOf(DeviceCapability.SEND_TAB),
                false,
                null
            ),
            Device(
                "c613492b-37be-49f6-a107-c5f33c7b6ee5",
                "FirefoxSync on Android 14",
                DeviceType.MOBILE,
                false,
                System.currentTimeMillis(),
                listOf(DeviceCapability.SEND_TAB),
                false,
                null
            )
        )

        PreviewThemeNew {
            SyncDeviceRow(targets = targets, sendTab = {}, closeDrawer = {})
        }
    }
}
