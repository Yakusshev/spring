package com.yakushev.spring.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.loop.CalculateLengthUseCase
import com.yakushev.spring.domain.loop.GenerateApplesUseCase
import com.yakushev.spring.domain.loop.HandleAppleCollisionScenario
import com.yakushev.spring.domain.loop.HandleSnakeCollisionScenario
import com.yakushev.spring.domain.loop.MoveSnakeUseCase
import com.yakushev.spring.domain.model.ApplePointModel
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.domain.model.SnakeModel
import com.yakushev.spring.domain.usecases.GetAppleListStateUseCase
import com.yakushev.spring.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.domain.usecases.GetSnakeLengthUseCase
import com.yakushev.spring.domain.usecases.GetSnakeStateUseCase
import com.yakushev.spring.domain.usecases.InitGameUseCase
import com.yakushev.spring.domain.usecases.SetDirectionUseCase
import com.yakushev.spring.domain.usecases.SetPlayStateUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameViewModel @Inject constructor(
    private val initGameUseCase: InitGameUseCase,
    private val getPlayStateUseCase: GetPlayStateUseCase,
    private val setGameStateUseCase: SetPlayStateUseCase,
    private val moveSnakeUseCase: MoveSnakeUseCase,
    private val getSnakeStateUseCase: GetSnakeStateUseCase,
    private val setDirectionUseCase: SetDirectionUseCase,
    private val getSnakeLengthUseCase: GetSnakeLengthUseCase,
    private val generateApplesUseCase: GenerateApplesUseCase,
    private val getAppleListStateUseCase: GetAppleListStateUseCase,
    private val handleAppleCollisionScenario: HandleAppleCollisionScenario,
    private val calculateLengthUseCase: CalculateLengthUseCase,
    private val handleSnakeCollisionScenario: HandleSnakeCollisionScenario,
) : ViewModel() {

    private var loopJob: Job? = null

    init {
        observeGameState()
    }

    internal fun getPlayState(): StateFlow<GameState> = getPlayStateUseCase()
    internal fun getSnakeState(): StateFlow<SnakeModel> = getSnakeStateUseCase()
    internal fun getSnakeLengthState(): StateFlow<Int> = getSnakeLengthUseCase()
    internal fun getAppleListState(): StateFlow<List<ApplePointModel>> = getAppleListStateUseCase()

    internal fun onInitScreen(width: Int, height: Int) {
        viewModelScope.launch {
            initGameUseCase(width, height, reset = false)
        }
    }

    internal fun onPlayClicked() {
        viewModelScope.launch {
            setGameStateUseCase(play = GameState.Play)
            calculateLengthUseCase()
        }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { setGameStateUseCase(play = GameState.Pause) }
    }

    internal fun onDirectionChanged(direction: DirectionEnum) {
        viewModelScope.launch { setDirectionUseCase(direction = direction) }
    }

    private fun observeGameState() {
        viewModelScope.launch {
            getPlayStateUseCase().collect { play ->
                loopJob?.cancel()
                if (play == GameState.Play) loopJob = loopJob()
            }
        }
    }

    private fun loopJob(): Job = viewModelScope.launch {
        var appleTime = 0L
        var current = System.currentTimeMillis()
        while (true) {
            while (System.currentTimeMillis() - current < Const.DELAY) {
                delay(1)
            }
            moveSnakeUseCase()
            handleAppleCollisionScenario()
            handleSnakeCollisionScenario()
            if (System.currentTimeMillis() - appleTime >= Const.APPLE_DELAY) {
                generateApplesUseCase()
                appleTime = System.currentTimeMillis()
            }
            current = System.currentTimeMillis()
        }
    }

    fun onResetClicked() {
        viewModelScope.launch {
            initGameUseCase(reset = true)
            setDirectionUseCase(direction = Const.DEFAULT_DIRECTION, reset = true)
            generateApplesUseCase(reset = true)
            setGameStateUseCase(GameState.Play)
        }
    }
}