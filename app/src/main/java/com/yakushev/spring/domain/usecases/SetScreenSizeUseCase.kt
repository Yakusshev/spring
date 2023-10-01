package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.Point
import javax.inject.Inject

class SetScreenSizeUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(width: Int, height: Int) {
        if (gameDataSource.getFieldWidth() == width
            && gameDataSource.getFieldHeight() == height) return

        gameDataSource.updateSnakeState { snake ->
            if (snake.pointList.isEmpty()) {
                snake.copy(pointList = defaultPointList(width, height))
            } else {
                snake
            }
        }
        gameDataSource.setFieldSize(width, height)
    }

    private fun defaultPointList(width: Int, height: Int): List<Point> =
        listOf(
            Point(x = width / 2, y = height / 2),
            Point(x = width / 2, y = height / 2 + (height * Const.SNAKE_LENGTH).toInt())
        )
}