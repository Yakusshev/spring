package com.yakushev.spring.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.yakushev.spring.core.appComponent
import com.yakushev.spring.di.DaggerAppComponent
import com.yakushev.spring.navigation.SetupNavHost
import com.yakushev.spring.ui.theme.SpringTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SpringTheme {
                val navController = rememberNavController()
                SetupNavHost(navController = navController)
            }
        }
    }

    private fun onInject() {
        appComponent.inject(this)
    }
}