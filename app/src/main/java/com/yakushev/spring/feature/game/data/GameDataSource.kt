package com.yakushev.spring.feature.game.data

import android.util.Log
import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.domain.model.ApplePointModel
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.EdgeEnum
import com.yakushev.spring.feature.game.domain.model.GameState
import com.yakushev.spring.feature.game.domain.model.Point
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

internal class GameDataSource {

    var snakeLength = 0f

    private val gameState = MutableStateFlow<GameState>(GameState.Pause)
    private val snakeState = MutableStateFlow<SnakeModel?>(value = null)
    private val directionState = MutableStateFlow(Const.DEFAULT_DIRECTION)
    private val lengthState = MutableStateFlow(value = 0f)
    private val appleListState = MutableStateFlow<List<ApplePointModel>>(emptyList())
    private val appleEatenState = MutableStateFlow(value = 0)
    private val displaySnakeLengthState = MutableStateFlow(value = false)
    private val bordersState = MutableStateFlow<List<List<Point>>>(value = emptyList())

    private var fieldHeight = 0f
    private var fieldWidth = 0f

    fun getGameState(): StateFlow<GameState> = gameState
    fun getSnakeState(): StateFlow<SnakeModel?> = snakeState
    fun getSnakeLengthState(): StateFlow<Float> = lengthState
    fun getDirectionState(): StateFlow<DirectionEnum> = directionState
    fun getAppleListState(): StateFlow<List<ApplePointModel>> = appleListState
    fun getAppleEatenState(): StateFlow<Int> = appleEatenState
    fun getDisplaySnakeLengthState(): StateFlow<Boolean> = displaySnakeLengthState
    fun getBordersState(): StateFlow<List<List<Point>>> = bordersState
    fun getFieldHeight(): Float = fieldHeight
    fun getFieldWidth(): Float = fieldWidth

    fun setFieldSize(width: Float, height: Float) {
        fieldWidth = width
        fieldHeight = height
        bordersState.value = level1(
            height = height,
            width = width,
            cellHalfWidth = getPointWidth() / 3,
        )
    }

    fun getPointWidth(): Float {
        val refSize = if (fieldHeight > fieldWidth) fieldHeight else fieldWidth
        return refSize * Const.SNAKE_BODY_COEF
    }

    suspend fun setGameState(play: GameState) {
        gameState.emit(play)
    }

    fun setSnake(snakeModel: SnakeModel) {
        snakeState.value = snakeModel
    }

    fun updateSnakeState(
        initSnake: SnakeModel? = null,
        function: ((state: SnakeModel) -> SnakeModel),
    ) {
        snakeState.update { state ->
            state?.let(function) ?: run {
                Log.d(this::class.simpleName, "updateSnakeState: snake is null")
                initSnake
            }
        }
    }

    fun updateSnakeLength(length: Float) {
        lengthState.update { length }
    }

    fun updateDirectionState(function: (state: DirectionEnum) -> DirectionEnum) {
        directionState.update { state -> function(state) }
    }

    fun updateAppleListState(function: (state: List<ApplePointModel>) -> List<ApplePointModel>) {
        appleListState.update(function)
    }

    fun updateAndGetAppleEaten(function: (state: Int) -> Int): Int =
        appleEatenState.updateAndGet(function)

    fun updateDisplaySnakeLength(display: Boolean) {
        displaySnakeLengthState.update { display }
    }

    fun getSnakeDefaultPoints(): List<SnakePointModel> =
        listOf(
            SnakePointModel(
                x = fieldWidth / 2,
                y = fieldHeight / 2,
                vx = 0f,
                vy = -Const.SNAKE_SPEED,
                edge = EdgeEnum.EMPTY,
            ),
            SnakePointModel(
                x = fieldWidth / 2,
                y = fieldHeight / 2 + snakeLength,
                vx = 0f,
                vy = -Const.SNAKE_SPEED,
                edge = EdgeEnum.EMPTY,
            )
        )
}
