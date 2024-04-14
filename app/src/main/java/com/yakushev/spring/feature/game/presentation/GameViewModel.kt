package com.yakushev.spring.feature.game.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.loop.GenerateApplesUseCase
import com.yakushev.spring.feature.game.domain.loop.HandleAppleCollisionScenario
import com.yakushev.spring.feature.game.domain.loop.HandleBorderCollisionScenario
import com.yakushev.spring.feature.game.domain.loop.HandleSnakeCollisionScenario
import com.yakushev.spring.feature.game.domain.loop.MoveSnakeUseCase
import com.yakushev.spring.feature.game.domain.loop.UpdateSnakeLengthUseCase
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.GameStage
import com.yakushev.spring.feature.game.domain.usecases.GetAppleEatenStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetAppleListStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetDelayUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetDisplaySnakeLengthStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetSnakeLengthUseCase
import com.yakushev.spring.feature.game.domain.usecases.GetSnakeStateUseCase
import com.yakushev.spring.feature.game.domain.usecases.InitGameUseCase
import com.yakushev.spring.feature.game.domain.usecases.ResetGameScenario
import com.yakushev.spring.feature.game.domain.usecases.SetDirectionUseCase
import com.yakushev.spring.feature.game.domain.usecases.SetPlayStateUseCase
import com.yakushev.spring.feature.game.presentation.mapper.toMultiPath
import com.yakushev.spring.feature.game.presentation.mapper.toSnakeUiModel
import com.yakushev.spring.feature.game.presentation.model.GameUiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
internal class GameViewModel @Inject constructor(
    private val initGameUseCase: InitGameUseCase,
    private val getPlayStageUseCase: GetPlayStateUseCase,
    private val setGameStageUseCase: SetPlayStateUseCase,
    private val moveSnakeUseCase: MoveSnakeUseCase,
    private val getSnakeStateUseCase: GetSnakeStateUseCase,
    private val setDirectionUseCase: SetDirectionUseCase,
    private val getSnakeLengthUseCase: GetSnakeLengthUseCase,
    private val generateApplesUseCase: GenerateApplesUseCase,
    private val getAppleListStateUseCase: GetAppleListStateUseCase,
    private val handleAppleCollisionScenario: HandleAppleCollisionScenario,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
    private val handleSnakeCollisionScenario: HandleSnakeCollisionScenario,
    private val handleBorderCollisionScenario: HandleBorderCollisionScenario,
    private val getDelayUseCase: GetDelayUseCase,
    private val getDisplaySnakeLengthStateUseCase: GetDisplaySnakeLengthStateUseCase,
    private val getAppleEatenStateUseCase: GetAppleEatenStateUseCase,
    private val resetGameScenario: ResetGameScenario,
    private val gameDataSource: GameDataSource,
) : ViewModel() {

    private val gameUiState = MutableStateFlow<GameUiModel?>(value = null)

    private var directionChangeJob: Job? = null
    private var initGameJob: Job? = null

    init {
        observeGameState()
    }

    fun getGameUiState(): StateFlow<GameUiModel?> = gameUiState
    fun getGameStage(): StateFlow<GameStage> = getPlayStageUseCase()
    fun getAppleEatenState(): StateFlow<Int> = getAppleEatenStateUseCase()
    fun getSnakeLengthState(): StateFlow<Float> = getSnakeLengthUseCase()
    fun getDisplaySnakeLengthState(): StateFlow<Boolean> = getDisplaySnakeLengthStateUseCase()

    fun onInitScreen(width: Float, height: Float) {
        val initGame = initGameJob == null
        initGameJob = viewModelScope.launch {
            gameDataSource.setFieldSize(width, height)
            if (initGame) initGameUseCase()
            updateGameState()
        }
    }

    internal fun onPlayClicked() {
        viewModelScope.launch {
            setGameStageUseCase(play = GameStage.Play)
            updateSnakeLengthUseCase()
        }
    }

    internal fun onBackPressed() {
        when (getGameStage().value) {
            GameStage.Pause -> {}
            GameStage.Play -> onPauseClicked()
            is GameStage.Potracheno -> resetGame(GameStage.Pause)
        }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { setGameStageUseCase(play = GameStage.Pause) }
    }

    internal fun onDirectionChanged(direction: DirectionEnum) {
        directionChangeJob?.cancel()
        directionChangeJob = viewModelScope.launch { setDirectionUseCase(direction = direction) }
    }

    private fun observeGameState() {
        viewModelScope.launch {
            val jobList: MutableList<Job> = mutableListOf()
            getGameStage().collect { gameStage ->
                val delay = getDelayUseCase().toInt().milliseconds.inWholeNanoseconds
                jobList.forEach { job -> job.cancel() }
                jobList.clear()
                when (gameStage) {
                    GameStage.Pause -> {}
                    GameStage.Play -> {
                        jobList.addAll(
                            listOf(
                                loopJob(delay = delay.toFloat(), print = true, moveSnakeUseCase::invoke),
                                loopJob(delay = delay / 2f) { handleAppleCollisionScenario() },
                                loopJob(delay = delay / 2f) { handleSnakeCollisionScenario() },
                                loopJob(delay = delay / 2f) { handleBorderCollisionScenario() },
                                loopJob(delay = delay.toFloat()) { updateGameState() }
                            )
                        )
                    }
                    is GameStage.Potracheno -> {}
                }
            }
        }
    }

    private suspend fun updateGameState() {
        val snake = getSnakeStateUseCase().value?.toSnakeUiModel() ?: run {
            Log.d("###", "${this::class.simpleName} updateGameState: snake is null")
            return
        }
        gameUiState.emit(
            value = GameUiModel(
                snakePathList = snake.pathList,
                snakeWidth = snake.width,
                appleList = getAppleListStateUseCase().value,
                borderPathList = listOf(gameDataSource.getBordersState().value.toMultiPath()),
            )
        )
    }

    private fun loopJob(
        delay: Float,
        print: Boolean = false,
        function: suspend (deviation: Float) -> Unit,
    ): Job = viewModelScope.launch(Dispatchers.Default) {
        var lastSec = System.nanoTime()
        var previous = delay.toLong() + 1
        var total = 0L
        var count = 0
        while (true) {
            previous = measureTime {
                if (System.nanoTime() - lastSec >= 1.seconds.inWholeNanoseconds && print) {
                    Log.d("###", "${this::class.simpleName} average calculation time = ${total / 1000000f / count} ms")
                    lastSec = System.nanoTime()
                    total = 0
                    count = 0
                }
                if (previous < delay) {
                    delay(timeMillis = (delay - previous).toLong().nanoseconds.inWholeMilliseconds)
                }
                //if (print) Log.d("###", "${this::class.simpleName} loopJob calculation duration = ${previous / 1000000f}")
                val deviation = previous.toFloat() / delay
                function(deviation)
            }.inWholeNanoseconds.also { prev ->
                total += prev
                count++
            }
        }
    }

    internal fun onResetClicked() {
        resetGame(GameStage.Play)
    }

    private fun resetGame(gameStage: GameStage) {
        viewModelScope.launch {
            resetGameScenario()
            setGameStageUseCase(gameStage)
        }
    }
}
