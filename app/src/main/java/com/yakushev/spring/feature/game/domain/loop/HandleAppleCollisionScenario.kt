package com.yakushev.spring.feature.game.domain.loop

import android.util.Log
import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import javax.inject.Inject

internal class HandleAppleCollisionScenario @Inject constructor(
    private val dataSource: GameDataSource,
    private val generateApplesUseCase: GenerateApplesUseCase,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
) {
    operator fun invoke() {
        val snake = dataSource.getSnakeState().value ?: run {
            Log.e(this::class.simpleName, "snake is null")
            return
        }
        val apples = dataSource.getAppleListState().value
        val radius = snake.width * Const.APPLE_COLLISION_COEF

        val newApples = apples.toMutableList()

        apples.forEach { apple ->
            val xCollision = snake.pointList.first().x in apple.x - radius..apple.x + radius
            val yCollision = snake.pointList.first().y in apple.y - radius..apple.y + radius
            if (xCollision && yCollision) {
                newApples.remove(apple)
                grow()
                updateSnakeLengthUseCase()
            }
        }
        dataSource.updateAppleListState { newApples }
        if (newApples.isEmpty()) generateApplesUseCase()
    }

    private fun grow() {
        dataSource.updateAndGetAppleEaten { eaten -> eaten + 1 }
        dataSource.updateSnakeState { snake ->
            val newList = snake.pointList.toMutableList()
            val tailPoint = snake.pointList.last()
            newList.removeLast()
            newList.add(tailPoint.grow())
            dataSource.snakeLength += Const.GROW
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
