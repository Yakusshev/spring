package com.yakushev.spring.domain.loop

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.SnakePointModel
import javax.inject.Inject

class HandleAppleCollisionScenario @Inject constructor(
    private val dataSource: GameDataSource,
    private val generateApplesUseCase: GenerateApplesUseCase,
    private val calculateLengthUseCase: UpdateSnakeLengthUseCase
) {
    suspend operator fun invoke() {
        val snake = dataSource.getSnakeState().value
        val apples = dataSource.getAppleListState().value
        val radius = snake.width

        val newApples = apples.toMutableList()

        apples.forEach { apple ->
            val xCollision = snake.pointList.first().x in apple.x - radius..apple.x + radius
            val yCollision = snake.pointList.first().y in apple.y - radius..apple.y + radius
            if (xCollision && yCollision) {
                newApples.remove(apple)
                grow()
                calculateLengthUseCase()
            }
        }
        dataSource.updateAppleListState { newApples }
        if (newApples.isEmpty()) generateApplesUseCase()
    }

    private suspend fun grow() {
        dataSource.updateSnakeState { snake ->
            val newList = snake.pointList.toMutableList()
            val tailPoint = snake.pointList.last()
            newList.removeLast()
            newList.add(tailPoint.grow())
            snake.copy(pointList = newList)
        }
    }

    private fun SnakePointModel.grow(): SnakePointModel =
        when (getDirection()) {
            DirectionEnum.UP -> copy(y = y + Const.GROW)
            DirectionEnum.DOWN -> copy(y = y - Const.GROW)
            DirectionEnum.RIGHT -> copy(x = x - Const.GROW)
            DirectionEnum.LEFT -> copy(x = x + Const.GROW)
            DirectionEnum.STOP -> throw Exception("last point direction can't be STOP")
        }
}