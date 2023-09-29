package com.yakushev.spring.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.yakushev.spring.presentation.game.GameScreen
import com.yakushev.spring.presentation.mainmenu.MainMenuScreen

sealed class Screens(val route: String) {
    object Splash : Screens(route = ScreenRoute.SPLASH)
    object MainMenu : Screens(route = ScreenRoute.MAIN_MENU)
    object Game : Screens(route = ScreenRoute.GAME)
}

@Composable
fun SetupNavHost(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory
) {
    NavHost(
        navController = navController,
        startDestination = Screens.MainMenu.route
    ) {
        composable(route = Screens.Splash.route) {

        }
        dialog(route = Screens.MainMenu.route) {

        }
        composable(route = Screens.MainMenu.route) {
            MainMenuScreen(
                viewModelFactory = viewModelFactory,
                playClick = { navController.navigate(Screens.Game.route) }
            )
        }

        composable(route = Screens.Game.route) {
            GameScreen()
        }
    }
}