package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.loop.UpdateSnakeLengthUseCase
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import javax.inject.Inject

internal class InitGameUseCase @Inject constructor(
    private val gameDataSource: GameDataSource,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
) {
    operator fun invoke(
        width: Float = gameDataSource.getFieldWidth(),
        height: Float = gameDataSource.getFieldHeight(),
        reset: Boolean,
    ) {
        if (gameDataSource.getFieldWidth() == width
            && gameDataSource.getFieldHeight() == height
            && !reset) return

        gameDataSource.setFieldSize(width, height)

        gameDataSource.snakeLength = getInitialSnakeLength(height)
        gameDataSource.updateSnakeState(
            initSnake = SnakeModel(
                pointList = gameDataSource.getSnakeDefaultPoints(),
                width = gameDataSource.getPointWidth(),
            ),
            function =  { snake -> snake }
        )
        if (reset) {
            gameDataSource.updateAndGetAppleEaten { 0 }
            updateSnakeLengthUseCase()
        }
    }

    private fun getInitialSnakeLength(height: Float): Float {
        val h1 = (height * Const.SNAKE_LENGTH).toInt()
        val h1mod = h1 % Const.SNAKE_SPEED
        return h1 - h1mod
    }
}
