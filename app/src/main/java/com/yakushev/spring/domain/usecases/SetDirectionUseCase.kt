package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.GameState
import javax.inject.Inject
import kotlin.math.abs

class SetDirectionUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(direction: DirectionEnum, reset: Boolean = false) {
        if (reset) {
            gameDataSource.setDirection(direction)
            return
        }
        if (gameDataSource.getGameState().value != GameState.Play) return
        val oldDirection = gameDataSource.getDirectionState().value
        when (oldDirection) {
            direction -> return
            direction.opposite() -> return
            else -> {}
        }
        gameDataSource.updateSnakeState { snake ->
            if (snake.pointList.size >= 3) {
                val xDiff = abs(snake.pointList[0].x - snake.pointList[1].x)
                if (xDiff < snake.width && xDiff != 0f) return@updateSnakeState snake
                val yDiff = abs(snake.pointList[0].y - snake.pointList[1].y)
                if (yDiff < snake.width && yDiff != 0f) return@updateSnakeState snake
            }
            gameDataSource.setDirection(direction)
            snake.copy(
                pointList = snake.pointList.toMutableList().apply {
                    add(1, first())
                }
            )
        }
    }
}