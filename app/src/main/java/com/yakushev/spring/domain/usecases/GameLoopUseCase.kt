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
        val edgePointState = MutableStateFlow<PointModel?>(null)

        dataSource.updateSnakeState { snake ->
            val lastPointDirection = snake.pointList.getLastPointDirection()
            snake.copy(
                pointList = snake.pointList
                    .move(snake.width, lastPointDirection, edgePointState)
                    .toMutableList()
                    .addEdgePoints(edgePointState)
                    .removeCornerIfNeed(lastPointDirection)
                    .removeEdgeIfNeed()
            )
        }
    }

    private fun List<PointModel>.move(
        width: Int,
        lastPointDirection: DirectionEnum,
        edgePointState: MutableStateFlow<PointModel?>
    ): List<PointModel> {
        val headDirection = dataSource.getDirectionState().value
        return mapIndexed { index, point ->
            val direction = when (index) {
                0 -> headDirection
                lastIndex -> lastPointDirection
                else -> return@mapIndexed point
            }

            when (direction) {
                DirectionEnum.UP -> point.up(width, edgePointState)
                DirectionEnum.DOWN -> point.down(width, edgePointState)
                DirectionEnum.RIGHT -> point.right(width, edgePointState)
                DirectionEnum.LEFT -> point.left(width, edgePointState)
            }
        }
    }

    private fun MutableList<PointModel>.addEdgePoints(
        edgePointState: MutableStateFlow<PointModel?>
    ): MutableList<PointModel> {
        edgePointState.value?.let { point ->
            add(1, point.copy(edge = EdgeEnum.INPUT))
            add(1, first().copy(edge = EdgeEnum.OUTPUT))
        }
        return this
    }

    private fun MutableList<PointModel>.removeCornerIfNeed(
        lastPointDirection: DirectionEnum
    ): MutableList<PointModel> {
        val lastPoint = this[lastIndex]
        val penultimatePoint = this[lastIndex - 1]

        val removePoint = when (lastPointDirection) {
            DirectionEnum.UP -> lastPoint.y <= penultimatePoint.y
            DirectionEnum.DOWN -> lastPoint.y >= penultimatePoint.y
            DirectionEnum.RIGHT -> lastPoint.x >= penultimatePoint.x
            DirectionEnum.LEFT -> lastPoint.x <= penultimatePoint.x
        }

        if (removePoint) remove(lastPoint)
        return this
    }

    private fun MutableList<PointModel>.removeEdgeIfNeed(): MutableList<PointModel> {
        val lastPoint = this[lastIndex]

        when (lastPoint.edge) {
            EdgeEnum.INPUT -> remove(lastPoint)
            EdgeEnum.OUTPUT -> {
                remove(lastPoint)
                add(lastPoint.copy(edge = EdgeEnum.EMPTY))
            }
            EdgeEnum.EMPTY -> {}
        }
        return this
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

    private fun PointModel.right(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + SNAKE_SPEED else teleport(
                -width,
                edgePointState
            )
        )
    }

    private fun PointModel.left(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            x = if (x > 0 - width) x - SNAKE_SPEED else teleport(
                dataSource.getFieldWidth(),
                edgePointState
            )
        )
    }

    private fun PointModel.up(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            y = if (y > 0 - width) y - SNAKE_SPEED else teleport(
                dataSource.getFieldHeight(),
                edgePointState
            )
        )
    }

    private fun PointModel.down(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + SNAKE_SPEED else teleport(
                -width,
                edgePointState
            )
        )
    }

    private fun PointModel.teleport(
        coordinate: Int,
        edgePointState: MutableStateFlow<PointModel?>
    ): Int {
        edgePointState.value = this
        return coordinate
    }

    companion object {
        const val REMOVE_CORNER_RANGE = SNAKE_SPEED * 2
    }
}