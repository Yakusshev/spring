package com.yakushev.spring.feature.game.data

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.domain.model.ApplePointModel
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.GameState
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class GameDataSource {

    var snakeLength = 0f

    private val gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState.Pause)
    private val snakeState: MutableStateFlow<SnakeModel> = MutableStateFlow(SnakeModel.empty)
    private val directionState: MutableStateFlow<DirectionEnum> = MutableStateFlow(Const.DEFAULT_DIRECTION)
    private val lengthState: MutableStateFlow<Float> = MutableStateFlow(value = 0f)
    private val appleListState: MutableStateFlow<List<ApplePointModel>> = MutableStateFlow(emptyList())
    private val appleEatenState = MutableStateFlow(value = 0)
    private val displaySnakeLengthState = MutableStateFlow(value = false)

    private var fieldHeight = 0f
    private var fieldWidth = 0f

    fun getGameState(): StateFlow<GameState> = gameState
    fun getSnakeState(): StateFlow<SnakeModel> = snakeState
    fun getSnakeLengthState(): StateFlow<Float> = lengthState
    fun getDirectionState(): StateFlow<DirectionEnum> = directionState
    fun getAppleListState(): StateFlow<List<ApplePointModel>> = appleListState
    fun getAppleEatenState(): StateFlow<Int> = appleEatenState
    fun getDisplaySnakeLengthState(): StateFlow<Boolean> = displaySnakeLengthState
    fun getFieldHeight(): Float = fieldHeight
    fun getFieldWidth(): Float = fieldWidth

    fun setFieldSize(width: Float, height: Float) {
        fieldWidth = width
        fieldHeight = height
    }

    suspend fun setGameState(play: GameState) {
        gameState.emit(play)
    }

    fun updateSnakeState(function: (state: SnakeModel) -> SnakeModel) {
        snakeState.update { state -> function(state) }
    }

    fun updateSnakeLength(length: Float) {
        lengthState.update { length }
    }

    fun updateDirectionState(function: (state: DirectionEnum) -> DirectionEnum) {
        directionState.update { state -> function(state) }
    }

    fun updateAppleListState(function: (state: List<ApplePointModel>) -> List<ApplePointModel>) {
        appleListState.update { state -> function(state) }
    }

    fun updateAndGetAppleEaten(function: (state: Int) -> Int): Int =
        appleEatenState.updateAndGet(function)

    fun updateDisplaySnakeLength(display: Boolean) {
        displaySnakeLengthState.update { display }
    }
}