package com.yakushev.spring.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yakushev.spring.presentation.game.compose.GameScreen

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

        composable(route = Route.GAME) {
            GameScreen(
                viewModelFactory = viewModelFactory
            )
        }
    }
}