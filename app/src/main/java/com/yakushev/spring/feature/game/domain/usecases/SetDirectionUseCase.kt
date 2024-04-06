package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.GameState
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import com.yakushev.spring.core.log
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.abs

class SetDirectionUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke(direction: DirectionEnum, reset: Boolean = false) {
        if (reset) {
            dataSource.updateDirectionState { direction }
            return
        }
        if (dataSource.getGameState().value != GameState.Play) return

        when (dataSource.getDirectionState().value) {
            direction -> return
            direction.opposite() -> return
            else -> {}
        }

        val snake = dataSource.getSnakeState().value

        if (snake.pointList.size >= 3) {
            val xDiff = abs(snake.pointList[0].x - snake.pointList[1].x).log("setDirectionUseCase xDiff")
            val yDiff = abs(snake.pointList[0].y - snake.pointList[1].y).log("setDirectionUseCase yDiff")
            val xNearMiss = xDiff < snake.width && xDiff != 0f
            val yNearMiss = yDiff < snake.width && yDiff != 0f
            val samePoint = yDiff == 0f && xDiff == 0f
            if (xNearMiss || yNearMiss || samePoint) return
        }


        val fieldWidth = dataSource.getFieldWidth()
        val fieldHeight = dataSource.getFieldHeight()

        while (true) {
            val head = dataSource.getSnakeState().value.pointList.first()
            if (!checkEdgeX(head, fieldWidth) && !checkEdgeY(head, fieldHeight)) break
            delay(1)
        }

        dataSource.updateDirectionState { direction }
        dataSource.updateSnakeState { snakeModel ->
            snakeModel.copy(
                pointList = snakeModel.pointList.toMutableList().apply {
                    this[0] = first().setDirection(direction)
                    add(1, first())
                }
            )
        }

        direction.log("setDirectionUseCase direction")
    }

    private fun checkEdgeY(
        head: SnakePointModel,
        fieldHeight: Float
    ) = head.vy != 0f && (head.y - head.vy * 1.5 <= 0
            || head.y + head.vy * 1.5 >= fieldHeight
            || head.y <= 0 || head.y >= fieldHeight
            || head.y in -head.vy * 1.5..head.vy * 1.5
            || head.y in fieldHeight - head.vy * 1.5..fieldHeight + head.vy * 1.5)

    private fun checkEdgeX(
        head: SnakePointModel,
        fieldWidth: Float
    ): Boolean =
        head.vx != 0f && (head.x - head.vx * 1.5 <= 0
                || head.x + head.vx * 1.5 >= fieldWidth
                || head.x <= 0f || head.x >= fieldWidth
                || head.x in -head.vx * 1.5..head.vx * 1.5
                || head.x in fieldWidth - head.vx..fieldWidth + head.vx)


    private fun SnakePointModel.setDirection(direction: DirectionEnum): SnakePointModel =
        when (direction) {
            DirectionEnum.UP -> copy(vy = -Const.SNAKE_SPEED, vx = 0f)
            DirectionEnum.DOWN -> copy(vy = Const.SNAKE_SPEED, vx = 0f)
            DirectionEnum.RIGHT -> copy(vy = 0f, vx = Const.SNAKE_SPEED)
            DirectionEnum.LEFT -> copy(vy = 0f, vx = -Const.SNAKE_SPEED)
            DirectionEnum.STOP -> throw Exception("impossible to set DirectionEnum.STOP")
        }
}