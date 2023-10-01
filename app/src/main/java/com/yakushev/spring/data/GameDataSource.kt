package com.yakushev.spring.data

import com.yakushev.spring.domain.model.ApplePointModel
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
    private val appleListState: MutableStateFlow<List<ApplePointModel>> = MutableStateFlow(emptyList())

    private var fieldHeight = 0
    private var fieldWidth = 0

    private var snakeBodySize = 0

    fun getPlayState(): StateFlow<Boolean> = playState
    fun getSnakeState(): StateFlow<SnakeModel> = snakeState
    fun getSnakeLengthState(): StateFlow<Int> = lengthState
    fun getDirectionState(): StateFlow<DirectionEnum> = directionState
    fun getAppleListState(): StateFlow<List<ApplePointModel>> = appleListState
    fun getFieldHeight(): Int = fieldHeight
    fun getFieldWidth(): Int = fieldWidth

    fun setFieldSize(width: Int, height: Int) {
        fieldWidth = width
        fieldHeight = height
    }

    suspend fun setPlay(play: Boolean) {
        playState.emit(play)
    }

    fun updateSnakeState(function: (state: SnakeModel) -> SnakeModel) {
        snakeState.update { state ->
            function(state)
        }
    }

    fun updateSnakeLength(length: Int) {
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