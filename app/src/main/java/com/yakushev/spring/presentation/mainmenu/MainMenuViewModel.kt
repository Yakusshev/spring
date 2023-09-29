package com.yakushev.spring.presentation.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.domain.GameConstants
import com.yakushev.spring.domain.usecases.SetScreenSizeUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainMenuViewModel @Inject constructor(
    private val setScreenSizeUseCase: SetScreenSizeUseCase
) : ViewModel() {
    private val playState = MutableStateFlow(false)
    private val snakeState = MutableStateFlow(SnakeUiState(10, 10))

    init {
        gameLoop()
    }

    internal fun getPlayState(): StateFlow<Boolean> = playState.asStateFlow()
    internal fun getSnakeState(): StateFlow<SnakeUiState> = snakeState.asStateFlow()

    internal fun onInitScreen(width: Int, height: Int) {
        setScreenSizeUseCase(width, height)
    }

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