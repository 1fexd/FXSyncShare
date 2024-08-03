package fe.firefoxsync

import androidx.annotation.Keep
import fe.android.compose.route.util.Route1
import fe.android.compose.route.util.RouteData
import fe.android.compose.route.util.route


@Keep
data class LoginRouteData(val authUrl: String) : RouteData {
    companion object : Route1<LoginRouteData, String>(
        Argument(LoginRouteData::authUrl),
        ::LoginRouteData
    )
}

object Routes {
    const val Main = "route__main"
    val Login = route(
        "route__login",
        route = LoginRouteData
    )
}
