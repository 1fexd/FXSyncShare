@file:OptIn(ExperimentalUuidApi::class)

package fe.fxsyncshare.composable.page.main

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.card.AlertCard
import fe.fxsyncshare.activity.bottomsheet.BottomSheetActivity
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@Composable
fun RandomLinkCard() {
    val context = LocalContext.current
    var link by remember { mutableStateOf(generateLink()) }

    AlertCard(
        headline = text("Random link"),
        subtitle = text(link.toString()),
        icon = Icons.Rounded.Link.iconPainter,
        iconContentDescription = null,
        onClick = {
            context.startActivity(Intent(Intent.ACTION_SEND, link).apply {
                component = ComponentName(context, BottomSheetActivity::class.java)
            })
        },
        content = {
            Button(onClick = {
                link = generateLink()
            }) {
                Text(text = "New")
            }
        },
    )
}

private fun generateLink(): Uri {
    return "https://${Uuid.generateV4().toHexString()}.com".toUri()
}
