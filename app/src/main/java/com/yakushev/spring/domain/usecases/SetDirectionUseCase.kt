package com.yakushev.spring.domain.usecases

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.Point
import com.yakushev.spring.domain.model.SnakeState
import javax.inject.Inject
import kotlin.math.abs

class SetDirectionUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(direction: Direction) {
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
                if (xDiff < snake.width * 1.5 && xDiff != 0) return@updateSnakeState snake
                val yDiff = abs(snake.pointList[0].y - snake.pointList[1].y)
                if (yDiff < snake.width * 1.5 && yDiff != 0) return@updateSnakeState snake
            }
            gameDataSource.setDirection(direction)
            snake.copy(
                pointList = snake.pointList.toMutableList().apply {
                    removeAt(0)
                    add(0, snake.getCornerPoint(oldDirection))
                    add(0, snake.getCornerPointHead(direction, oldDirection))
                    Log.d("###", "SetDirectionUseCase: ${this.size}")
                    Log.d("###", "invoke: ${this}")
                }
            )
        }
    }

    private fun SnakeState.getCornerPoint(direction: Direction): Point {
        val point = pointList.first()
        Log.d("###", "getCornerPoint: width $width")
        Log.d("###", "getCornerPoint: direction $direction")
        return when (direction) {
            Direction.UP -> point.copy(y = point.y + width / 2)
            Direction.DOWN -> point.copy(y = point.y - width / 2)
            Direction.RIGHT -> point.copy(x = point.x - width / 2)
            Direction.LEFT -> point.copy(x = point.x + width / 2)
        }
    }

    private fun SnakeState.getCornerPointHead(direction: Direction, oldDirection: Direction): Point {
        val point = pointList.first()
        Log.d("###", "getCornerPoint: width $width")
        Log.d("###", "getCornerPoint: direction $direction")
        val new = when (oldDirection) {
            Direction.UP -> point.copy(y = point.y + width / 2)
            Direction.DOWN -> point.copy(y = point.y - width / 2)
            Direction.RIGHT -> point.copy(x = point.x - width / 2)
            Direction.LEFT -> point.copy(x = point.x + width / 2)
        }
        return when (direction) {
            Direction.UP -> new.copy(y = point.y - width / 2)
            Direction.DOWN -> new.copy(y = point.y + width / 2)
            Direction.RIGHT -> new.copy(x = point.x + width / 2)
            Direction.LEFT -> new.copy(x = point.x - width / 2)
        }
    }
}