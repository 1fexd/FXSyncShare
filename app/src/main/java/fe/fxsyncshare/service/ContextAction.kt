package fe.fxsyncshare.service

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContextAction(val text: String) : Parcelable
