package com.yakushev.spring.data

import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.ApplePointModel
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.domain.model.SnakeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameDataSource {

    private val gameState: MutableStateFlow<GameState> = MutableStateFlow(GameState.Pause)
    private val snakeState: MutableStateFlow<SnakeModel> = MutableStateFlow(SnakeModel.empty)
    private val directionState: MutableStateFlow<DirectionEnum> = MutableStateFlow(Const.DEFAULT_DIRECTION)
    private val lengthState: MutableStateFlow<Float> = MutableStateFlow(value = 0f)
    private val appleListState: MutableStateFlow<List<ApplePointModel>> = MutableStateFlow(emptyList())

    private var fieldHeight = 0f
    private var fieldWidth = 0f

    private var snakeBodySize = 0

    fun getGameState(): StateFlow<GameState> = gameState
    fun getSnakeState(): StateFlow<SnakeModel> = snakeState
    fun getSnakeLengthState(): StateFlow<Float> = lengthState
    fun getDirectionState(): StateFlow<DirectionEnum> = directionState
    fun getAppleListState(): StateFlow<List<ApplePointModel>> = appleListState
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
        snakeState.update { state ->
            function(state)
        }
    }

    fun updateSnakeLength(length: Float) {
        lengthState.update { length }
    }

    fun setDirection(direction: DirectionEnum) {
        directionState.update { direction }
    }

    fun updateAppleListState(function: (state: List<ApplePointModel>) -> List<ApplePointModel>) {
        appleListState.update { state ->
            function(state)
        }
    }
}