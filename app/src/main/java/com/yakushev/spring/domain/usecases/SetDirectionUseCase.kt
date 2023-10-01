package com.yakushev.spring.domain.usecases

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.DirectionEnum
import javax.inject.Inject
import kotlin.math.abs

class SetDirectionUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(direction: DirectionEnum) {
        if (!gameDataSource.getPlayState().value) return
        val oldDirection = gameDataSource.getDirectionState().value
        when (oldDirection) {
            direction -> return
            direction.opposite() -> return
            else -> {}
        }
        gameDataSource.updateSnakeState { snake ->
            Log.d("###", "invoke: ${snake.pointList}")
            if (snake.pointList.size >= 3) {
                val xDiff = abs(snake.pointList[0].x - snake.pointList[1].x)
                if (xDiff < snake.width && xDiff != 0) return@updateSnakeState snake
                val yDiff = abs(snake.pointList[0].y - snake.pointList[1].y)
                if (yDiff < snake.width && yDiff != 0) return@updateSnakeState snake
            }
            gameDataSource.setDirection(direction)
            snake.copy(
                pointList = snake.pointList.toMutableList().apply {
//                    removeAt(0)
                    add(1, first())
//                    add(0, snake.getCornerPointHead(direction, oldDirection))
                    Log.d("###", "SetDirectionUseCase: ${this.size}")
                    Log.d("###", "invoke: ${this}")
                }
            )
        }
    }
}