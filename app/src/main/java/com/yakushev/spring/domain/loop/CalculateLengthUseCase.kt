package com.yakushev.spring.domain.loop

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import javax.inject.Inject
import kotlin.math.abs

class CalculateLengthUseCase @Inject constructor(
    private val dataSource: GameDataSource,
) {
    operator fun invoke() {
        try {
            dataSource.getSnakeState().value.pointList.calculateLength()
        } catch (e: Exception) {
            dataSource.updateSnakeLength(-1)
            e.printStackTrace()
        }
    }

    private fun List<SnakePointModel>.calculateLength(): List<SnakePointModel> =
        apply {
            var length = 0
            forEachIndexed { index, point ->
                if (point.edge == EdgeEnum.OUTPUT) return@forEachIndexed
                length += abs(point.x - this[index + 1].x) +
                        abs(point.y - this[index + 1].y)
                if (index == lastIndex - 1) {
                    dataSource.updateSnakeLength(length)
                    return@apply
                }
            }
        }
}