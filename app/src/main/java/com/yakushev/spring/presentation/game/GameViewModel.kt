package com.yakushev.spring.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.SnakeModel
import com.yakushev.spring.domain.usecases.GameLoopUseCase
import com.yakushev.spring.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.domain.usecases.GetSnakeStateUseCase
import com.yakushev.spring.domain.usecases.SetDirectionUseCase
import com.yakushev.spring.domain.usecases.SetPlayStateUseCase
import com.yakushev.spring.domain.usecases.SetScreenSizeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameViewModel @Inject constructor(
    private val setScreenSizeUseCase: SetScreenSizeUseCase,
    private val getPlayStateUseCase: GetPlayStateUseCase,
    private val setPlayStateUseCase: SetPlayStateUseCase,
    private val gameLoopUseCase: GameLoopUseCase,
    private val getSnakeStateUseCase: GetSnakeStateUseCase,
    private val setDirectionUseCase: SetDirectionUseCase,
) : ViewModel() {

    private var loopJob: Job? = null

    init {
        observeGameState()
    }

    internal fun getPlayState(): StateFlow<Boolean> = getPlayStateUseCase()
    internal fun getSnakeState(): StateFlow<SnakeModel> = getSnakeStateUseCase()

    internal fun onInitScreen(width: Int, height: Int) {
        viewModelScope.launch {
            setScreenSizeUseCase(width, height)
        }
    }

    internal fun onPlayClicked() {
        viewModelScope.launch { setPlayStateUseCase(play = true) }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { setPlayStateUseCase(play = false) }
    }

    internal fun onDirectionChanged(direction: DirectionEnum) {
        viewModelScope.launch { setDirectionUseCase(direction = direction) }
    }

    private fun observeGameState() {
        viewModelScope.launch {
            getPlayStateUseCase().collect { play ->
                loopJob?.cancel()
                if (play) loopJob = loopJob()
            }
        }
    }

    private fun loopJob(): Job = viewModelScope.launch {
        var current = System.currentTimeMillis()
        while (true) {
            while (System.currentTimeMillis() - current < Const.DELAY) {
                delay(1)
            }
            gameLoopUseCase()
//            delay(Const.DELAY)
            current = System.currentTimeMillis()
        }
    }

}