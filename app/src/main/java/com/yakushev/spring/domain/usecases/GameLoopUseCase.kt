package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.Point
import javax.inject.Inject

class GameLoopUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        val headDirection = dataSource.getDirectionState().value

        dataSource.updateSnakeState { snake ->
            val lastPointDirection = snake.pointList.getLastPointDirection()
            snake.copy(
                pointList = snake.pointList.mapIndexed { index, point ->
                    val direction = snake.pointList.run {
                        when (index) {
                            0 -> headDirection
                            lastIndex -> lastPointDirection
                            else -> return@mapIndexed point
                        }
                    }
                    when (direction) {
                        Direction.UP -> point.up(snake.width)
                        Direction.DOWN -> point.down(snake.width)
                        Direction.RIGHT -> point.right(snake.width)
                        Direction.LEFT -> point.left(snake.width)
                    }
                }.checkRemoveRange(lastPointDirection)
            )
        }
    }

    private fun List<Point>.checkRemoveRange(
        lastPointDirection: Direction
    ): List<Point> {
        val lastPoint = this[lastIndex]
        val penultimatePoint = this[lastIndex - 1]

        val removePoint = when (lastPointDirection) {
            Direction.UP -> lastPoint.y <= penultimatePoint.y
            Direction.DOWN -> lastPoint.y >= penultimatePoint.y
            Direction.RIGHT -> lastPoint.x >= penultimatePoint.x
            Direction.LEFT -> lastPoint.x <= penultimatePoint.x
        }

        return if (removePoint) {
            toMutableList().apply {
                remove(lastPoint)
            }
        } else {
            this
        }
    }

    private fun List<Point>.getLastPointDirection(): Direction {
        val lastPoint = this[lastIndex]
        val previousPoint = this[lastIndex - 1]
        return when {
            previousPoint.x > lastPoint.x -> Direction.RIGHT
            previousPoint.x < lastPoint.x -> Direction.LEFT
            previousPoint.y > lastPoint.y -> Direction.DOWN
            else -> Direction.UP
        }
    }

    private fun Point.right(width: Int): Point {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + SNAKE_SPEED else -width
        )
    }

    private fun Point.left(width: Int): Point {
        return copy(
            x = if (x > 0 - width) x - SNAKE_SPEED else dataSource.getFieldWidth()
        )
    }

    private fun Point.up(width: Int): Point {
        return copy(
            y = if (y > 0 - width) y - SNAKE_SPEED else dataSource.getFieldHeight()
        )
    }

    private fun Point.down(width: Int): Point {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + SNAKE_SPEED else -width
        )
    }

    companion object {
        const val REMOVE_CORNER_RANGE = SNAKE_SPEED * 2
    }
}