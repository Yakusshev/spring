package com.yakushev.spring.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.yakushev.spring.presentation.game.GameScreen
import com.yakushev.spring.presentation.mainmenu.MainMenuScreen

@Composable
fun SetupNavHost(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
    exitClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Route.GAME
    ) {
        composable(route = Route.SPLASH) {

        }
        dialog(route = Route.MAIN_MENU) {
            MainMenuScreen(
                viewModelFactory = viewModelFactory,
                playClick = {
                    navController.navigate(Route.GAME) {
                        popUpTo(Route.MAIN_MENU) {
                            inclusive = true
                        }
                    }
                },
                exitClick = exitClick
            )
        }

        composable(route = Route.GAME) {
            GameScreen(
                viewModelFactory = viewModelFactory,
                onBackPressed = { navController.navigate(Route.MAIN_MENU) }
            )
        }
    }
}