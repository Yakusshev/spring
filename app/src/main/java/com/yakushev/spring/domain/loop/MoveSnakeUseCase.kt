package com.yakushev.spring.domain.loop

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import com.yakushev.spring.utils.print
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MoveSnakeUseCase @Inject constructor(
    private val dataSource: GameDataSource,
    private val calculateSnakeLengthUseCase: CalculateSnakeLengthUseCase,
) {
    private var speed = SNAKE_SPEED
    private var lastPointDirection: DirectionEnum = DirectionEnum.UP

    suspend operator fun invoke(deviation: Float) {
        val inputEdgePointState = MutableStateFlow<SnakePointModel?>(value = null)
        val outputEdgePointState = MutableStateFlow<SnakePointModel?>(value = null)
        speed = SNAKE_SPEED //* deviation
        Log.d("###", "invoke: speed = $speed")

        dataSource.updateSnakeState { snake ->
//            Log.d("###", "move: snake = $snake")
            snake.copy(
                pointList = snake.pointList
                    .apply { lastPointDirection = getLastPointDirection() }
                    .move(inputEdgePointState, outputEdgePointState, deviation)
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
    ): MutableList<SnakePointModel> {
        return mapIndexed { index, point ->
            val direction = when (index) {
                0 -> dataSource.getDirectionState().value
                lastIndex -> lastPointDirection
                else -> return@mapIndexed point
            }

            val head = index == 0
            when (direction) {
                DirectionEnum.UP -> point.up(edgePointState, outputEdgePointState, head)
                DirectionEnum.DOWN -> point.down(edgePointState, outputEdgePointState, head)
                DirectionEnum.RIGHT -> point.right(edgePointState, outputEdgePointState, head)
                DirectionEnum.LEFT -> point.left(edgePointState, outputEdgePointState, head)
            }
        }.toMutableList()
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
        val tailPoint = this[lastIndex]
        val preTailPoint = this[lastIndex - 1]

        val removePoint = when (lastPointDirection) {
            DirectionEnum.UP -> tailPoint.y <= preTailPoint.y
            DirectionEnum.DOWN -> tailPoint.y >= preTailPoint.y
            DirectionEnum.RIGHT -> tailPoint.x >= preTailPoint.x
            DirectionEnum.LEFT -> tailPoint.x <= preTailPoint.x
        }

        if (removePoint) remove(tailPoint)
        return this
    }

//    private fun MutableList<SnakePointModel>.removeEdgeIfNeed(radius: Float): MutableList<SnakePointModel> {
//        val tailPoint = this[lastIndex]
//        val preTailPoint = this[lastIndex - 1]
//        val xNearMiss = tailPoint.x in preTailPoint.x - radius..preTailPoint.x + radius
//        val yNearMiss = tailPoint.y in preTailPoint.y - radius..preTailPoint.y + radius
//        if (!(xNearMiss || yNearMiss)) return this
//
//        when (preTailPoint.edge) {
//            EdgeEnum.INPUT -> remove(preTailPoint)
//            EdgeEnum.OUTPUT -> {
//                remove(preTailPoint)
////                add(preTailPoint.copy(edge = EdgeEnum.EMPTY))
//            }
//            EdgeEnum.EMPTY -> {}
//        }
//        return this
//    }

    private fun MutableList<SnakePointModel>.removeEdgeIfNeed(): MutableList<SnakePointModel> {
        val lastPoint = this[lastIndex]

        Log.d("###", "... ")
        Log.d("###", "removeEdgeIfNeed: ${print()}")
        when (lastPoint.edge) {
            EdgeEnum.INPUT -> remove(lastPoint)
            EdgeEnum.OUTPUT -> {
                remove(lastPoint)
                add(lastPoint.copy(edge = EdgeEnum.EMPTY))
            }
            EdgeEnum.EMPTY -> {}
        }
        Log.d("###", "removeEdgeIfNeed: ${print()}")
        return this
    }

    private fun List<SnakePointModel>.getLastPointDirection(): DirectionEnum {
        val tailPoint = this[lastIndex]
        val preTailPoint = this[lastIndex - 1]
        return when {
            preTailPoint.x > tailPoint.x -> DirectionEnum.RIGHT
            preTailPoint.x < tailPoint.x -> DirectionEnum.LEFT
            preTailPoint.y > tailPoint.y -> DirectionEnum.DOWN
            else -> DirectionEnum.UP
        }
    }

    private fun SnakePointModel.right(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + speed else {
                if (head) outputEdgePointState.value = this.copy(x = 0f)
                teleport(0 + speed, edgePointState, head)
            }
        )
    }

    private fun SnakePointModel.left(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            x = if (x > 0) x - speed else {
                if (head) outputEdgePointState.value = this.copy(x = dataSource.getFieldWidth())
                teleport(dataSource.getFieldWidth() - speed, edgePointState, head)
            }
        )
    }

    private fun SnakePointModel.up(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            y = if (y > 0) y - speed else {
                if (head) outputEdgePointState.value = this.copy(y = dataSource.getFieldHeight())
                teleport(dataSource.getFieldHeight() - speed, edgePointState, head)
            }
        )
    }

    private fun SnakePointModel.down(
        edgePointState: MutableStateFlow<SnakePointModel?>,
        outputEdgePointState: MutableStateFlow<SnakePointModel?>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + speed else {
                if (head) outputEdgePointState.value = this.copy(y = 0f)
                teleport(0 + speed, edgePointState, head)
            }
        )
    }

    //todo если вызвался телепорт - удалить инпут и аутпут
    private fun SnakePointModel.teleport(
        coordinate: Float,
        edgePointState: MutableStateFlow<SnakePointModel?>,
        head: Boolean,
    ): Float {
        if (head) edgePointState.value = this
        return coordinate
    }

    companion object {
    }
}