package fe.fxsyncshare.activity.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import fe.fxsyncshare.composable.login.MainRoute
import fe.fxsyncshare.Routes

@Composable
fun MainNavHost(
    navController: NavHostController,
    navigate: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    NavHost(
//        modifier = Modifier.background(Color.Red),
        navController = navController,
        startDestination = Routes.Main
    ) {
        composable(route = Routes.Main) {
            MainRoute(navController = navController)
        }

//        argumentRouteComposable(route = Routes.Login) { entry, route ->
////            LoginRoute(navController = navController, authUrl = route.authUrl)
//        }
    }
}