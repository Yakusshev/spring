package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.utils.log
import javax.inject.Inject
import kotlin.math.abs

class SetDirectionUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(direction: DirectionEnum, reset: Boolean = false) {
        if (reset) {
            gameDataSource.updateDirectionState { direction }
            return
        }
        if (gameDataSource.getGameState().value != GameState.Play) return

        gameDataSource.updateDirectionState { oldDirection ->
            when (oldDirection) {
                direction -> return@updateDirectionState oldDirection
                direction.opposite() -> return@updateDirectionState oldDirection
                else -> {}
            }
            var update = false
            gameDataSource.updateSnakeState { snake ->
                if (snake.pointList.size >= 3) {
                    val xDiff = abs(snake.pointList[0].x - snake.pointList[1].x).log("setDirectionUseCase xDiff")
                    val yDiff = abs(snake.pointList[0].y - snake.pointList[1].y).log("setDirectionUseCase yDiff")
                    val xNearMiss = xDiff < snake.width && xDiff != 0f
                    val yNearMiss = yDiff < snake.width && yDiff != 0f
                    val samePoint = yDiff == 0f && xDiff == 0f
                    if (xNearMiss || yNearMiss || samePoint) return@updateSnakeState snake
                }
                update = true
                snake.copy(
                    pointList = snake.pointList.toMutableList().apply { add(1, first()) }
                )
            }
            if (update) direction.log("setDirectionUseCase direction") else oldDirection
        }
    }
}