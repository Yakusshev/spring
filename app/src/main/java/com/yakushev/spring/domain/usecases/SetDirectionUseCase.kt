package com.yakushev.spring.domain.usecases

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.Point
import javax.inject.Inject

class SetDirectionUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(direction: Direction) {
        when (gameDataSource.getDirectionState().value) {
            direction -> return
            direction.opposite() -> return
            else -> {}
        }
        gameDataSource.updateSnakeState { snake ->
            snake.copy(
                pointList = snake.pointList.toMutableList().apply {
                    add(1, snake.pointList.first())
                    Log.d("###", "SetDirectionUseCase: ${this.size}")
                }
            )
        }
        gameDataSource.setDirection(direction)
    }

    private fun Point.getCornerPoint(): Point {

        return this
    }
}