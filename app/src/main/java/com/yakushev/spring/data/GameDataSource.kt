package com.yakushev.spring.data

import android.util.Log
import com.yakushev.spring.domain.Const.SNAKE_BODY_COEF
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.SnakeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class GameDataSource {

    private val playState: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
    private val snakeState: MutableStateFlow<SnakeModel> = MutableStateFlow(SnakeModel.empty)
    private val directionState: MutableStateFlow<DirectionEnum> = MutableStateFlow(DirectionEnum.UP)
    private val lengthState: MutableStateFlow<Int> = MutableStateFlow(value = 0)

    private var fieldHeight = 0
    private var fieldWidth = 0

    private var snakeBodySize = 0

    fun setFieldSize(width: Int, height: Int) {
        fieldWidth = width
        fieldHeight = height
    }

    fun getFieldHeight(): Int = fieldHeight

    fun getFieldWidth(): Int = fieldWidth

    suspend fun setPlay(play: Boolean) {
        Log.d("###", "setPlay: $play")
        playState.emit(play)
    }

    fun getPlayState(): StateFlow<Boolean> = playState

    fun getSnakeState(): StateFlow<SnakeModel> = snakeState

    fun updateSnakeState(function: (state: SnakeModel) -> SnakeModel) {
        snakeState.update { state ->
            function(state)
        }
    }

    fun updateSnakeLength(length: Int) {
        lengthState.update { length }
    }

    fun getSnakeLengthState(): StateFlow<Int> = lengthState

    fun getDirectionState(): StateFlow<DirectionEnum> = directionState

    fun setDirection(direction: DirectionEnum) {
        directionState.update { direction }
    }
}