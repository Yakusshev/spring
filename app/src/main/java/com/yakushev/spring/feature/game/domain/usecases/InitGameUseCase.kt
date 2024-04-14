package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import javax.inject.Inject

internal class InitGameUseCase @Inject constructor(
    private val gameDataSource: GameDataSource,
) {
    operator fun invoke() {
        gameDataSource.snakeLength = getInitialSnakeLength()
        gameDataSource.updateSnakeState(
            initSnake = SnakeModel(
                pointList = gameDataSource.getSnakeDefaultPoints(),
                width = gameDataSource.getPointWidth(),
            ),
            function = { snake -> snake }
        )
    }

    private fun getInitialSnakeLength(): Float {
        val h1 = (gameDataSource.getFieldHeight() * Const.SNAKE_LENGTH).toInt()
        val h1mod = h1 % Const.SNAKE_SPEED
        return h1 - h1mod
    }
}
