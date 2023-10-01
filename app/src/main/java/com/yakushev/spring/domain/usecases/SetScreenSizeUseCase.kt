package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.PointModel
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

    private fun defaultPointList(width: Int, height: Int): List<PointModel> =
        listOf(
            PointModel(x = width / 2, y = height / 2, edge = EdgeEnum.EMPTY),
            PointModel(x = width / 2, y = height / 2 + (height * Const.SNAKE_LENGTH).toInt(), edge = EdgeEnum.EMPTY)
        )
}