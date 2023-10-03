package com.yakushev.spring.domain.loop

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MoveSnakeUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    private var speed = SNAKE_SPEED

    suspend operator fun invoke(deviation: Float) {
        val inputEdgePointState = MutableStateFlow<SnakePointModel?>(value = null)
        val outputEdgePointState = MutableStateFlow<SnakePointModel?>(value = null)
        speed = SNAKE_SPEED //* deviation
        Log.d("###", "invoke: speed = $speed")

        dataSource.updateSnakeState { snake ->
            Log.d("###", "move: snake = $snake")
            snake.copy(
                pointList = snake.pointList
                    .move(inputEdgePointState, outputEdgePointState, deviation)
                    .toMutableList()
                    .addEdgePoints(inputEdgePointState, outputEdgePointState)
                    .removeCornerIfNeed()
                    .removeEdgeIfNeed()
            )
        }
    }

    private fun List<SnakePointModel>.move(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        deviation: Float
    ): List<SnakePointModel> {
        val headDirection = dataSource.getDirectionState().value
        return mapIndexed { index, point ->
            val direction = when (index) {
                0 -> headDirection
                lastIndex -> getLastPointDirection()
                else -> return@mapIndexed point
            }

            when (direction) {
                DirectionEnum.UP -> point.up(edgePointState, outputEdgePointState, deviation)
                DirectionEnum.DOWN -> point.down(edgePointState, outputEdgePointState, deviation)
                DirectionEnum.RIGHT -> point.right(edgePointState, outputEdgePointState, deviation)
                DirectionEnum.LEFT -> point.left(edgePointState, outputEdgePointState, deviation)
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
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        deviation: Float
    ): SnakePointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + speed else {
                outputEdgePointState.value = this.copy(x = 0f)
                teleport(
                    0 + speed,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.left(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        deviation: Float
    ): SnakePointModel {
        return copy(
            x = if (x > 0) x - speed else {
                outputEdgePointState.value = this.copy(x = dataSource.getFieldWidth())
                teleport(
                    dataSource.getFieldWidth() - speed,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.up(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        deviation: Float
    ): SnakePointModel {
        return copy(
            y = if (y > 0) y - speed else {
                outputEdgePointState.value = this.copy(y = dataSource.getFieldHeight())
                teleport(
                    dataSource.getFieldHeight() - speed,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.down(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        deviation: Float
    ): SnakePointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + speed else {
                outputEdgePointState.value = this.copy(y = 0f)
                teleport(
                    0 + speed,
                    edgePointState,
                )
            }
        )
    }

    private fun SnakePointModel.teleport(
        coordinate: Float,
        edgePointState: MutableStateFlow<SnakePointModel?>,
    ): Float {
        edgePointState.value = this
        return coordinate
    }

    companion object {
    }
}