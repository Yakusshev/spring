package com.yakushev.spring.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.yakushev.spring.navigation.SetupNavHost
import com.yakushev.spring.ui.theme.SpringTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onInject()

        setContent {
            SpringTheme {
                val navController = rememberNavController()
                SetupNavHost(
                    navController = navController,
                    viewModelFactory = viewModelFactory
                )
            }
        }
    }

    private fun onInject() {
        appComponent.inject(this)
    }
}