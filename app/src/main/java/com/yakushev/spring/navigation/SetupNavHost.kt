package com.yakushev.spring.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.yakushev.spring.feature.game.presentation.GameScreen
import com.yakushev.spring.feature.test.presentation.TestScreen

@Composable
fun SetupNavHost(
    navController: NavHostController,
    viewModelFactory: ViewModelProvider.Factory,
) {
    NavHost(
        navController = navController,
        startDestination = Route.GAME
    ) {
        dialog(route = Route.SETTINGS) {
            TestScreen(viewModelFactory = viewModelFactory)
        }
        composable(route = Route.GAME) {
            GameScreen(
                viewModelFactory = viewModelFactory,
                navController = navController
            )
        }
    }
}