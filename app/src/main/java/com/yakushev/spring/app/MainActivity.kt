package com.yakushev.spring.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.yakushev.spring.core.theme.SpringTheme
import com.yakushev.spring.navigation.SetupNavHost
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private fun onInject() {
        (applicationContext as SpringApp).getAppComponent().inject(activity = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onInject()
        super.onCreate(savedInstanceState)

        setContent {
            SpringTheme {
                val navController = rememberNavController()
                SetupNavHost(
                    navController = navController,
                    viewModelFactory = viewModelFactory,
                )
            }
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}