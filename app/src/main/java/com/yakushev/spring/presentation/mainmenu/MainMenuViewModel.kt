package com.yakushev.spring.presentation.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object GameConstants {
    const val STEP = 10
    const val DELAY = 1000L / 60L
}

class MainMenuViewModel(

) : ViewModel() {
    private val playState = MutableStateFlow(false)
    private val snakeState = MutableStateFlow(SnakeUiState(10, 10))

    init {
        gameLoop()
    }

    internal fun getPlayState(): StateFlow<Boolean> = playState.asStateFlow()
    internal fun getSnakeState(): StateFlow<SnakeUiState> = snakeState.asStateFlow()

    internal fun onPlayClicked() {
        viewModelScope.launch { playState.emit(true) }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { playState.emit(false) }
    }

    private fun gameLoop() = viewModelScope.launch {
        getPlayState().collect { playState ->
            while (playState) {
                delay(GameConstants.DELAY)
                step()
            }
        }
    }

    private suspend fun step() {
        snakeState.update { state ->
            state.copy(x = state.x + 10)
        }
    }



    data class SnakeUiState(
        val x: Int,
        val y: Int
    )
}