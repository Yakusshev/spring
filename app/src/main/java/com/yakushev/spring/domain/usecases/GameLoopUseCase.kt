package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.PointModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class GameLoopUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        val headDirection = dataSource.getDirectionState().value
        val edgePointState = MutableStateFlow<PointModel?>(null)

        dataSource.updateSnakeState { snake ->
            val lastPointDirection = snake.pointList.getLastPointDirection()
            snake.copy(
                pointList = snake.pointList.mapIndexed { index, point ->
                    val direction = snake.pointList.run {
                        when (index) {
                            0 -> headDirection
                            lastIndex -> lastPointDirection
                            else -> return@mapIndexed point
                        }
                    }
                    when (direction) {
                        DirectionEnum.UP -> point.up(snake.width, edgePointState)
                        DirectionEnum.DOWN -> point.down(snake.width, edgePointState)
                        DirectionEnum.RIGHT -> point.right(snake.width, edgePointState)
                        DirectionEnum.LEFT -> point.left(snake.width, edgePointState)
                    }
                }
                    .addEdgePoint(edgePointState)
                    .checkRemoveRange(lastPointDirection)
            )
        }
    }

    private fun List<PointModel>.addEdgePoint(edgePointState: MutableStateFlow<PointModel?>): List<PointModel> {
        return edgePointState.value?.let { point ->
            toMutableList().apply {
                add(1, point.copy(edge = EdgeEnum.INPUT))
                add(1, first().copy(edge = EdgeEnum.OUTPUT))
            }
        } ?: this
    }

    private fun List<PointModel>.checkRemoveRange(
        lastPointDirection: DirectionEnum
    ): List<PointModel> {
        val lastPoint = this[lastIndex]
        val penultimatePoint = this[lastIndex - 1]

        val removePoint = when (lastPointDirection) {
            DirectionEnum.UP -> lastPoint.y <= penultimatePoint.y
            DirectionEnum.DOWN -> lastPoint.y >= penultimatePoint.y
            DirectionEnum.RIGHT -> lastPoint.x >= penultimatePoint.x
            DirectionEnum.LEFT -> lastPoint.x <= penultimatePoint.x
        }


        return when {
            removePoint -> {
                toMutableList().apply {
                    remove(lastPoint)
                }
            }
            lastPoint.edge == EdgeEnum.INPUT -> {
                toMutableList().apply {
                    remove(lastPoint)
                }
            }
//            lastPoint.edge && size > 2 -> {
//                toMutableList().apply {
//                    remove(lastPoint)
//                }
//            }
            lastPoint.edge == EdgeEnum.OUTPUT /*&& size == 2*/ -> {
                toMutableList().apply {
                    remove(lastPoint)
                    add(lastPoint.copy(edge = EdgeEnum.EMPTY))
                }
            }
            else -> this
        }
    }

    private fun List<PointModel>.getLastPointDirection(): DirectionEnum {
        val lastPoint = this[lastIndex]
        val previousPoint = this[lastIndex - 1]
        return when {
            previousPoint.x > lastPoint.x -> DirectionEnum.RIGHT
            previousPoint.x < lastPoint.x -> DirectionEnum.LEFT
            previousPoint.y > lastPoint.y -> DirectionEnum.DOWN
            else -> DirectionEnum.UP
        }
    }

    private fun PointModel.right(width: Int, edgePointState: MutableStateFlow<PointModel?>): PointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + SNAKE_SPEED else teleport(-width, edgePointState)
        )
    }

    private fun PointModel.left(width: Int, edgePointState: MutableStateFlow<PointModel?>): PointModel {
        return copy(
            x = if (x > 0 - width) x - SNAKE_SPEED else teleport(dataSource.getFieldWidth(), edgePointState)
        )
    }

    private fun PointModel.up(width: Int, edgePointState: MutableStateFlow<PointModel?>): PointModel {
        return copy(
            y = if (y > 0 - width) y - SNAKE_SPEED else teleport(dataSource.getFieldHeight(), edgePointState)
        )
    }

    private fun PointModel.down(width: Int, edgePointState: MutableStateFlow<PointModel?>): PointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + SNAKE_SPEED else teleport(-width, edgePointState)
        )
    }

    private fun PointModel.teleport(coordinate: Int, edgePointState: MutableStateFlow<PointModel?>): Int {
        edgePointState.value = this
        return coordinate
    }

    companion object {
        const val REMOVE_CORNER_RANGE = SNAKE_SPEED * 2
    }
}