package com.yakushev.spring.feature.game.presentation

import android.util.Log
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.loop.GenerateApplesUseCase
import com.yakushev.spring.feature.game.domain.loop.HandleAppleCollisionScenario
import com.yakushev.spring.feature.game.domain.loop.HandleSnakeCollisionScenario
import com.yakushev.spring.feature.game.domain.loop.MoveSnakeUseCase
import com.yakushev.spring.feature.game.domain.loop.UpdateSnakeLengthUseCase
import com.yakushev.spring.feature.game.domain.model.ApplePointModel
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.GameState
import com.yakushev.spring.feature.game.domain.usecases.GetAppleEatenStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetAppleListStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetDelayUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetDisplaySnakeLengthStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetSnakeLengthUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetSnakeStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.InitGameUseCase
import com.yakushev.spring.feature.game.domain.usecases.SetDirectionUseCase
import com.yakushev.spring.feature.game.domain.usecases.SetPlayStateUseCase
import com.yakushev.spring.feature.game.presentation.mapper.toMultiPath
import com.yakushev.spring.feature.game.presentation.mapper.toSnakeUiModel
import com.yakushev.spring.feature.game.presentation.model.SnakeUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class GameViewModel @Inject constructor(
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
    private val getDelayUseCase: GetDelayUseCase,
    private val getDisplaySnakeLengthStateUseCase: GetDisplaySnakeLengthStateUseCase,
    private val getAppleEatenStateUseCase: GetAppleEatenStateUseCase,
    private val gameDataSource: GameDataSource,
) : ViewModel() {

    private var directionChangeJob: Job? = null
    private var initGameJob: Job? = null

    init {
        observeGameState()
    }

    internal fun getGameState(): StateFlow<GameState> = getPlayStateUseCase()
    internal fun getSnakeState(): Flow<SnakeUiModel> = getSnakeStateUseCase()
        .map { snakeModel -> snakeModel?.toSnakeUiModel() ?: SnakeUiModel.empty }

    internal fun getSnakeLengthState(): StateFlow<Float> = getSnakeLengthUseCase()
    internal fun getAppleListState(): StateFlow<List<ApplePointModel>> = getAppleListStateUseCase()
    internal fun getDisplaySnakeLengthState(): StateFlow<Boolean> = getDisplaySnakeLengthStateUseCase()
    internal fun getAppleEatenState(): StateFlow<Int> = getAppleEatenStateUseCase()
    fun getBordersState(): Flow<Path> = gameDataSource.getBordersState().map { it.toMultiPath() }

    internal fun onInitScreen(width: Float, height: Float) {
//        if (initGameJob?.isActive == true) return
        initGameJob = viewModelScope.launch {
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
        directionChangeJob?.cancel()
        directionChangeJob = viewModelScope.launch { setDirectionUseCase(direction = direction) }
    }

    private fun observeGameState() {
        viewModelScope.launch {
            val jobs: MutableList<Job> = mutableListOf()
            getGameState().collect { play ->
                val delay = getDelayUseCase()
                jobs.forEach { job -> job.cancel() }
                jobs.clear()
                if (play == GameState.Play) {
                    jobs.addAll(
                        listOf(
                            loopJob(delay = delay, print = false, moveSnakeUseCase::invoke),
                            loopJob(delay = delay / 4f) { handleAppleCollisionScenario() },
                            loopJob(delay = delay / 4f) { handleSnakeCollisionScenario() },
//                            loopJob { generateApplesUseCase() }
                        )
                    )
                } else {
                    jobs.clear()
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
            val deviation = (System.currentTimeMillis() - previous) / delay
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