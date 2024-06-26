package com.yakushev.spring.feature.game.domain.loop

import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.EdgeEnum
import javax.inject.Inject
import kotlin.math.abs

class CalculateSnakeLengthUseCase @Inject constructor(
    private val dataSource: GameDataSource,
) {
    operator fun invoke(): Float {
        val list = dataSource.getSnakeState().value.pointList.toList()
        var length = 0f
        list.forEachIndexed { index, point ->
            if (point.edge == EdgeEnum.OUTPUT || index >= list.lastIndex) return@forEachIndexed
            length += abs(point.x - list[index + 1].x) + abs(point.y - list[index + 1].y)
        }
        return length
    }
}