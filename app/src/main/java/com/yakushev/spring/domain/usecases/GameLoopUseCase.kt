package com.yakushev.spring.domain.usecases

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.PointModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.math.abs

class GameLoopUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        val inputEdgePointState = MutableStateFlow<PointModel?>(value = null)
        val outputEdgePointState = MutableStateFlow<PointModel?>(value = null)

        dataSource.updateSnakeState { snake ->
            val lastPointDirection = snake.pointList.getLastPointDirection()
            snake.copy(
                pointList = snake.pointList
                    .move(
                        snake.width,
                        lastPointDirection,
                        inputEdgePointState,
                        outputEdgePointState
                    )
                    .toMutableList()
                    .addEdgePoints(inputEdgePointState, outputEdgePointState)
                    .removeCornerIfNeed(lastPointDirection)
                    .removeEdgeIfNeed()
                    .calculateLength()
            )
        }
    }

    private fun List<PointModel>.calculateLength(): List<PointModel> =
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

    private fun List<PointModel>.move(
        width: Int,
        lastPointDirection: DirectionEnum,
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): List<PointModel> {
        val headDirection = dataSource.getDirectionState().value
        return mapIndexed { index, point ->
            val direction = when (index) {
                0 -> headDirection
                lastIndex -> lastPointDirection
                else -> return@mapIndexed point
            }

            when (direction) {
                DirectionEnum.UP -> point.up(width, edgePointState, outputEdgePointState)
                DirectionEnum.DOWN -> point.down(width, edgePointState, outputEdgePointState)
                DirectionEnum.RIGHT -> point.right(width, edgePointState, outputEdgePointState)
                DirectionEnum.LEFT -> point.left(width, edgePointState, outputEdgePointState)
            }
        }
    }

    private fun MutableList<PointModel>.addEdgePoints(
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): MutableList<PointModel> {
        edgePointState.value?.let { point ->
            add(1, point.copy(edge = EdgeEnum.INPUT))
            add(1, outputEdgePointState.value!!.copy(edge = EdgeEnum.OUTPUT))
            Log.d("###", "addEdgePoints: $this")
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
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(x = 0)
                teleport(
                    0 + SNAKE_SPEED,
                    edgePointState,
                    outputEdgePointState
                )
            }
        )
    }

    private fun PointModel.left(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            x = if (x > 0) x - SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(x = dataSource.getFieldWidth())
                teleport(
                    dataSource.getFieldWidth() - SNAKE_SPEED,
                    edgePointState,
                    outputEdgePointState
                )
            }
        )
    }

    private fun PointModel.up(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            y = if (y > 0) y - SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(y = dataSource.getFieldHeight())
                teleport(
                    dataSource.getFieldHeight() - SNAKE_SPEED,
                    edgePointState,
                    outputEdgePointState
                )
            }
        )
    }

    private fun PointModel.down(
        width: Int,
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): PointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + SNAKE_SPEED else {
                outputEdgePointState.value = this.copy(y = 0)
                teleport(
                    0 + SNAKE_SPEED,
                    edgePointState,
                    outputEdgePointState
                )
            }
        )
    }

    private fun PointModel.teleport(
        coordinate: Int,
        edgePointState: MutableStateFlow<PointModel?>,
        outputEdgePointState: MutableStateFlow<PointModel?>
    ): Int {
        edgePointState.value = this
        return coordinate
    }

    companion object {
        const val REMOVE_CORNER_RANGE = SNAKE_SPEED * 2
    }
}