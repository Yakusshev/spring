package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.loop.UpdateSnakeLengthUseCase
import com.yakushev.spring.feature.game.domain.model.EdgeEnum
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import javax.inject.Inject

class InitGameUseCase @Inject constructor(
    private val gameDataSource: GameDataSource,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
) {
    suspend operator fun invoke(
        width: Float = gameDataSource.getFieldWidth(),
        height: Float = gameDataSource.getFieldHeight(),
        reset: Boolean
    ) {
        val notExecute = gameDataSource.getFieldWidth() == width
                && gameDataSource.getFieldHeight() == height
                && !reset
        if (notExecute) return

        val refSize = if (height > width) height else width

        gameDataSource.snakeLength = getInitialSnakeLength(height)
        gameDataSource.updateSnakeState { snake ->
            if (snake.pointList.isEmpty() || reset) {
                snake.copy(
                    pointList = defaultPointList(width, height),
                    width = (refSize * Const.SNAKE_BODY_COEF)
                )
            } else {
                snake
            }
        }
        gameDataSource.setFieldSize(width, height)
        if (reset) {
            gameDataSource.updateAndGetAppleEaten { 0 }
            updateSnakeLengthUseCase()
        }
    }

    private fun defaultPointList(width: Float, height: Float): List<SnakePointModel> =
        listOf(
            SnakePointModel(
                x = width / 2,
                y = height / 2,
                vx = 0f,
                vy = -Const.SNAKE_SPEED,
                edge = EdgeEnum.EMPTY,
            ),
            SnakePointModel(
                x = width / 2,
                y = height / 2 + gameDataSource.snakeLength,
                vx = 0f,
                vy = -Const.SNAKE_SPEED,
                edge = EdgeEnum.EMPTY,
            )
        )

    private fun getInitialSnakeLength(height: Float): Float {
        val h1 = (height * Const.SNAKE_LENGTH).toInt()
        val h1mod = h1 % Const.SNAKE_SPEED
        return h1 - h1mod
    }
}