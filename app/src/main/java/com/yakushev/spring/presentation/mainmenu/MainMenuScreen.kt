package com.yakushev.spring.presentation.mainmenu

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainMenuScreen(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: MainMenuViewModel = viewModel(factory = viewModelFactory),
    playClick: () -> Unit,
    exitClick: () -> Unit //todo
) {

    (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0f)
}
