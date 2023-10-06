package com.yakushev.spring.presentation.game

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.loop.GenerateApplesUseCase
import com.yakushev.spring.domain.loop.HandleAppleCollisionScenario
import com.yakushev.spring.domain.loop.HandleSnakeCollisionScenario
import com.yakushev.spring.domain.loop.MoveSnakeUseCase
import com.yakushev.spring.domain.loop.UpdateSnakeLengthUseCase
import com.yakushev.spring.domain.model.ApplePointModel
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.domain.usecases.GetAppleListStateUseCase
import com.yakushev.spring.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.domain.usecases.GetSnakeLengthUseCase
import com.yakushev.spring.domain.usecases.GetSnakeStateUseCase
import com.yakushev.spring.domain.usecases.InitGameUseCase
import com.yakushev.spring.domain.usecases.SetDirectionUseCase
import com.yakushev.spring.domain.usecases.SetPlayStateUseCase
import com.yakushev.spring.presentation.game.mapper.toSnakeUiModel
import com.yakushev.spring.presentation.game.model.SnakeUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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
    private val calculateLengthUseCase: UpdateSnakeLengthUseCase,
    private val handleSnakeCollisionScenario: HandleSnakeCollisionScenario,
) : ViewModel() {

    private var directionChangeJob: Job? = null
    private var initGameJob: Job? = null

    init {
        observeGameState()
    }

    internal fun getGameState(): StateFlow<GameState> = getPlayStateUseCase()
    internal fun getSnakeState(): Flow<SnakeUiModel> = getSnakeStateUseCase()
        .map { snakeModel -> snakeModel.toSnakeUiModel() }

    internal fun getSnakeLengthState(): StateFlow<Float> = getSnakeLengthUseCase()
    internal fun getAppleListState(): StateFlow<List<ApplePointModel>> = getAppleListStateUseCase()

    internal fun onInitScreen(width: Float, height: Float) {
        if (initGameJob?.isActive == true) return
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

    internal fun onBackPressed() {
        when (getGameState().value) {
            GameState.Pause -> {}
            GameState.Play -> onPauseClicked()
            is GameState.Potracheno -> resetGame(GameState.Pause)
        }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { setGameStateUseCase(play = GameState.Pause) }
    }

    internal fun onDirectionChanged(direction: DirectionEnum) {
        if (directionChangeJob?.isActive == true) return
        viewModelScope.launch { setDirectionUseCase(direction = direction) }
    }

    private fun observeGameState() {
        viewModelScope.launch {
            val jobs: MutableList<Job> = mutableListOf()
            getGameState().collect { play ->
                jobs.forEach { job -> job.cancel() }
                jobs.clear()
                if (play == GameState.Play) {
                    jobs.addAll(
                        listOf(
                            loopJob(delay = Const.DELAY, print = false) { deviation -> moveSnakeUseCase(deviation) },
                            loopJob(delay = Const.DELAY / 4f) { handleAppleCollisionScenario() },
                            loopJob(delay = Const.DELAY / 4f) { handleSnakeCollisionScenario() },
//                            loopJob { generateApplesUseCase() }
                        )
                    )
                }
            }
        }
    }

    private fun loopJob(
        delay: Float,
        print: Boolean = false,
        function: suspend (deviation: Float) -> Unit,
    ): Job = viewModelScope.launch(Dispatchers.Default) {
        var previous = System.currentTimeMillis()
        while (true) {
            while (System.currentTimeMillis() - previous < delay) delay(timeMillis = 1)
            if (print) Log.d("###", "loopJob: ${System.currentTimeMillis() - previous}")
            val deviation = (System.currentTimeMillis() - previous) / Const.DELAY
            previous = System.currentTimeMillis()
            function(deviation)
        }
    }

    internal fun onResetClicked() {
        resetGame(GameState.Play)
    }

    private fun resetGame(gameState: GameState) {
        viewModelScope.launch {
            initGameUseCase(reset = true)
            setDirectionUseCase(direction = Const.DEFAULT_DIRECTION, reset = true)
            generateApplesUseCase(reset = true)
            calculateLengthUseCase()
            setGameStateUseCase(gameState)
        }
    }
}