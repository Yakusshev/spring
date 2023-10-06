package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.domain.model.SnakePointModel
import com.yakushev.spring.utils.log
import javax.inject.Inject
import kotlin.math.abs

class SetDirectionUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    operator fun invoke(direction: DirectionEnum, reset: Boolean = false) {
        if (reset) {
            dataSource.updateDirectionState { direction }
            return
        }
        if (dataSource.getGameState().value != GameState.Play) return

        dataSource.updateDirectionState { oldDirection ->
            when (oldDirection) {
                direction -> return@updateDirectionState oldDirection
                direction.opposite() -> return@updateDirectionState oldDirection
                else -> {}
            }
            var update = false
            dataSource.updateSnakeState { snake ->
                val head = snake.pointList.first()
//                val xCond = head.vx != 0f && (head.x - head.vx * 1.5 <= 0
//                        || head.x + head.vx * 1.5 >= dataSource.getFieldWidth())
//                val yCond = head.vy != 0f && (head.y - head.vy * 1.5 < 0
//                        || head.y + head.vy * 1.5 >= dataSource.getFieldHeight())
//                if (xCond || yCond) {
//                    runBlocking { delay(Const.DELAY.toLong() * 4) }
//                }
                if (snake.pointList.size >= 3) {
                    val xDiff =
                        abs(snake.pointList[0].x - snake.pointList[1].x).log("setDirectionUseCase xDiff")
                    val yDiff =
                        abs(snake.pointList[0].y - snake.pointList[1].y).log("setDirectionUseCase yDiff")
                    val xNearMiss = xDiff < snake.width && xDiff != 0f
                    val yNearMiss = yDiff < snake.width && yDiff != 0f
                    val samePoint = yDiff == 0f && xDiff == 0f
                    if (xNearMiss || yNearMiss || samePoint) return@updateSnakeState snake
                }
                update = true
                snake.copy(
                    pointList = snake.pointList.toMutableList().apply {
                        this[0] = first().setDirection(direction)
                        add(1, first()/*.copy(direction = direction)*/)
                    }
                )
            }
            if (update) direction.log("setDirectionUseCase direction") else oldDirection
        }
    }

    private fun SnakePointModel.setDirection(direction: DirectionEnum): SnakePointModel =
        when (direction) {
            DirectionEnum.UP -> copy(vy = -Const.SNAKE_SPEED, vx = 0f)
            DirectionEnum.DOWN -> copy(vy = Const.SNAKE_SPEED, vx = 0f)
            DirectionEnum.RIGHT -> copy(vy = 0f, vx = Const.SNAKE_SPEED)
            DirectionEnum.LEFT -> copy(vy = 0f, vx = -Const.SNAKE_SPEED)
            DirectionEnum.STOP -> throw Exception("impossible to set DirectionEnum.STOP")
        }
}