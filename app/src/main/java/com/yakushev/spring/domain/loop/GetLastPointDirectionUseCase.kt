package com.yakushev.spring.domain.loop

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.SnakePointModel
import javax.inject.Inject

class GetLastPointDirectionUseCase @Inject constructor(
    private val dataSource: GameDataSource,
) {
    operator fun invoke(): DirectionEnum =
        dataSource.getSnakeState().value.pointList.getLastPointDirection()

    private fun List<SnakePointModel>.getLastPointDirection(): DirectionEnum {
        val lastPoint = this[lastIndex]
        val previousPoint = this[lastIndex - 1]
        return when {
            previousPoint.x > lastPoint.x -> DirectionEnum.RIGHT
            previousPoint.x < lastPoint.x -> DirectionEnum.LEFT
            previousPoint.y > lastPoint.y -> DirectionEnum.DOWN
            else -> DirectionEnum.UP
        }
    }
}