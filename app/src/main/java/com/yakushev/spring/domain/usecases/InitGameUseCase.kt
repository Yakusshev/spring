package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import javax.inject.Inject

class InitGameUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(
        width: Int = gameDataSource.getFieldWidth(),
        height: Int = gameDataSource.getFieldHeight(),
        reset: Boolean
    ) {
        val notExecute = gameDataSource.getFieldWidth() == width
                && gameDataSource.getFieldHeight() == height
                && !reset
        if (notExecute) return

        val refSize = if (height > width) height else width

        gameDataSource.updateSnakeState { snake ->
            if (snake.pointList.isEmpty() || reset) {
                snake.copy(
                    pointList = defaultPointList(width, height),
                    width = (refSize * Const.SNAKE_BODY_COEF).toInt()
                )
            } else {
                snake
            }
        }
        gameDataSource.setFieldSize(width, height)
    }

    private fun defaultPointList(width: Int, height: Int): List<SnakePointModel> =
        listOf(
            SnakePointModel(x = width / 2, y = height / 2, edge = EdgeEnum.EMPTY),
            SnakePointModel(
                x = width / 2,
                y = height / 2 + calculateSnakeHeight(height),
                edge = EdgeEnum.EMPTY
            )
        )

    private fun calculateSnakeHeight(height: Int): Int {
        val h1 = (height * Const.SNAKE_LENGTH).toInt()
        val h1mod = h1 % Const.SNAKE_SPEED
        return h1 - h1mod
    }
}