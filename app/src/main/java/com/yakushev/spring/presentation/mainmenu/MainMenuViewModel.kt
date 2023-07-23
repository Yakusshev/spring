package com.yakushev.spring.presentation.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainMenuViewModel : ViewModel() {
    private val playState = MutableStateFlow(false)
    private val snakeState = MutableStateFlow(SnakeUiState(10, 10))

    internal fun getPlayState(): StateFlow<Boolean> = playState.asStateFlow()
    internal fun getSnakeState(): StateFlow<SnakeUiState> = snakeState.asStateFlow()

    internal fun onPlayClicked() {
        viewModelScope.launch { playState.emit(true) }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { playState.emit(false) }
    }




    data class SnakeUiState(
        val x: Int,
        val y: Int
    )
}