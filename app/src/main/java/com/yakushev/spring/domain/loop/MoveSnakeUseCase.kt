package com.yakushev.spring.domain.loop

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.abs

class MoveSnakeUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        val inputEdgePointState = MutableStateFlow<SnakePointModel?>(value = null)
        val outputEdgePointState = MutableStateFlow<SnakePointModel?>(value = null)

        dataSource.updateSnakeState { snake ->
            snake.copy(
                pointList = snake.pointList
                    .move(inputEdgePointState, outputEdgePointState)
                    .toMutableList()
                    .addEdgePoints(inputEdgePointState, outputEdgePointState)
                    .removeCornerIfNeed()
                    .removeEdgeIfNeed()
                    .calculateLength()
            )
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

    private fun List<SnakePointModel>.move(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>
    ): List<SnakePointModel> {
        val headDirection = dataSource.getDirectionState().value
        return mapIndexed { index, point ->
            val direction = when (index) {
                0 -> headDirection
                lastIndex -> getLastPointDirection()
                else -> return@mapIndexed point
            }

            when (direction) {
                DirectionEnum.UP -> point.up(edgePointState, outputEdgePointState)
                DirectionEnum.DOWN -> point.down(edgePointState, outputEdgePointState)
                DirectionEnum.RIGHT -> point.right(edgePointState, outputEdgePointState)
                DirectionEnum.LEFT -> point.left(edgePointState, outputEdgePointState)
            }
        }
    }

    private fun MutableList<SnakePointModel>.addEdgePoints(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>
    ): MutableList<SnakePointModel> {
        edgePointState.value?.let { point ->
            add(1, point.copy(edge = EdgeEnum.INPUT))
            add(1, outputEdgePointState.value!!.copy(edge = EdgeEnum.OUTPUT))
        }
        return this
    }

    private fun MutableList<SnakePointModel>.removeCornerIfNeed(): MutableList<SnakePointModel> {
        val lastPoint = this[lastIndex]
        val penultimatePoint = this[lastIndex - 1]

        val removePoint = when (getLastPointDirection()) {
            DirectionEnum.UP -> lastPoint.y <= penultimatePoint.y
            DirectionEnum.DOWN -> lastPoint.y >= penultimatePoint.y
            DirectionEnum.RIGHT -> lastPoint.x >= penultimatePoint.x
            DirectionEnum.LEFT -> lastPoint.x <= penultimatePoint.x
        }

        if (removePoint) remove(lastPoint)
        return this
    }

    private fun MutableList<SnakePointModel>.removeEdgeIfNeed(): MutableList<SnakePointModel> {
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

    private fun SnakePointModel.right(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>
    ): SnakePointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(x = 0)
                teleport(
                    0 + SNAKE_SPEED,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.left(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>
    ): SnakePointModel {
        return copy(
            x = if (x > 0) x - SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(x = dataSource.getFieldWidth())
                teleport(
                    dataSource.getFieldWidth() - SNAKE_SPEED,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.up(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>
    ): SnakePointModel {
        return copy(
            y = if (y > 0) y - SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(y = dataSource.getFieldHeight())
                teleport(
                    dataSource.getFieldHeight() - SNAKE_SPEED,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.down(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>
    ): SnakePointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(y = 0)
                teleport(
                    0 + SNAKE_SPEED,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.teleport(
        coordinate: Int,
        edgePointState: MutableStateFlow<SnakePointModel?>,
    ): Int {
        edgePointState.value = this
        return coordinate
    }

    companion object {
        const val REMOVE_CORNER_RANGE = SNAKE_SPEED * 2
    }
}