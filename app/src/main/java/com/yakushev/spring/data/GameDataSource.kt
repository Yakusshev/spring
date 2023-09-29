package com.yakushev.spring.data

import android.util.Log
import com.yakushev.spring.domain.GameConstants.SNAKE_BODY_COEF
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.SnakeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameDataSource {

    private val playState: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    private val snakeState: MutableStateFlow<SnakeState> = MutableStateFlow(SnakeState.empty)
    private val directionState: MutableStateFlow<Direction> = MutableStateFlow(Direction.RIGHT)

    private var fieldHeight = 0
    private var fieldWidth = 0

    private var snakeBodySize = 0

    fun setFieldSize(width: Int, height: Int) {
        fieldWidth = width
        fieldHeight = height
        snakeState.update { snake -> snake.copy(size = fieldHeight / SNAKE_BODY_COEF) }
    }

    fun getFieldHeight(): Int = fieldHeight

    fun getFieldWidth(): Int = fieldWidth

    suspend fun setPlay(play: Boolean) {
        Log.d("###", "setPlay: $play")
        playState.emit(play)
    }

    fun getPlayState(): StateFlow<Boolean> = playState

    fun getSnakeState(): StateFlow<SnakeState> = snakeState

    fun updateSnakeState(function: (state: SnakeState) -> SnakeState) {
        snakeState.update { state -> function(state) }
    }

    fun getDirectionState(): StateFlow<Direction> = directionState

    fun setDirection(direction: Direction) {
        directionState.update { direction }
    }
}