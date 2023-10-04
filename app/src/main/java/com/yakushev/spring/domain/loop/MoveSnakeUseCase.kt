package com.yakushev.spring.domain.loop

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import com.yakushev.spring.utils.log
import com.yakushev.spring.utils.toText
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
            Log.d("###", "move: snake = $snake")
            snake.copy(
                pointList = snake.pointList
                    .apply { lastPointDirection = getLastPointDirection() }
                    .toMutableList()
                    .move(deviation)
                    .removeCornerIfNeed()
            )
        }
    }

    private fun MutableList<SnakePointModel>.move(
        deviation: Float
    ): MutableList<SnakePointModel> {
        val head = when (dataSource.getDirectionState().value) {
            DirectionEnum.UP -> first().up(this, true)
            DirectionEnum.DOWN -> first().down(this, true)
            DirectionEnum.RIGHT -> first().right(this, true)
            DirectionEnum.LEFT -> first().left(this, true)
        }
        this[0] = head

        val tail = when (lastPointDirection) {
            DirectionEnum.UP -> last().up(this, false)
            DirectionEnum.DOWN -> last().down(this, false)
            DirectionEnum.RIGHT -> last().right(this, false)
            DirectionEnum.LEFT -> last().left(this, false).log("left tail")
        }
        this[lastIndex] = tail
        return this
    }

    private fun MutableList<SnakePointModel>.addEdgePoints(
        edgePointState: SnakePointModel,
        outputEdgePointState: SnakePointModel
    ): MutableList<SnakePointModel> {
        add(1, edgePointState.copy(edge = EdgeEnum.INPUT))
        add(1, outputEdgePointState.copy(edge = EdgeEnum.OUTPUT))
        return this
    }

    private fun MutableList<SnakePointModel>.removeCornerIfNeed(): MutableList<SnakePointModel> {
        val tailPoint = this[lastIndex]
        val preTailPoint = this[lastIndex - 1]//.also { point -> if (point.edge == EdgeEnum.INPUT || point.edge == EdgeEnum.OUTPUT) return this }

        val removePoint = when (lastPointDirection) {
            DirectionEnum.UP -> tailPoint.y <= preTailPoint.y
            DirectionEnum.DOWN -> tailPoint.y >= preTailPoint.y
            DirectionEnum.RIGHT -> tailPoint.x >= preTailPoint.x
            DirectionEnum.LEFT -> tailPoint.x <= preTailPoint.x
        }

        when {
            removePoint && preTailPoint.edge == EdgeEnum.INPUT -> removeEdgePoints()
            removePoint -> remove(tailPoint)
        }

//        if (removePoint) remove(tailPoint)
        return this
    }

    private fun MutableList<SnakePointModel>.removeEdgePoints(): MutableList<SnakePointModel> {
        Log.d("###", "removeEdgePoints: ${this.toText()}")
        findLast { point -> point.edge == EdgeEnum.OUTPUT }?.let { point -> remove(point) }
        findLast { point -> point.edge == EdgeEnum.INPUT }?.let { point -> remove(point) }
        Log.d("###", "removeEdgePoints: ${this.toText()}")
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
        list: MutableList<SnakePointModel>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + speed else {
                if (head) list.addEdgePoints(this, this.copy(x = 0f)).log("right tp head")
                else list.removeEdgePoints().log("right tp tail")
                0 + speed
            }
        )
    }

    private fun SnakePointModel.left(
        list: MutableList<SnakePointModel>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            x = if (x > 0) x - speed else {
                if (head) list.addEdgePoints(this, this.copy(x = dataSource.getFieldWidth())).log("left tp head")
                else list.removeEdgePoints().log("left tp tail")
                log("left tp")
                dataSource.getFieldWidth() - speed
            }
        )
    }

    // y > 0 && y - speed >= list[list.lastIndex - 1].y
    private fun SnakePointModel.up(
        list: MutableList<SnakePointModel>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            y = if (y > 0) y - speed else {
                if (head) list.addEdgePoints(this, this.copy(y = dataSource.getFieldHeight())).log("up tp head")
                else list.removeEdgePoints().log("up tp tail")
                dataSource.getFieldHeight() - speed
            }
        )
    }

    private fun SnakePointModel.down(
        list: MutableList<SnakePointModel>,
        head: Boolean
    ): SnakePointModel {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + speed else {
                if (head) list.addEdgePoints(this, this.copy(y = 0f)).log("down tp head")
                else list.removeEdgePoints().log("down tp tail")
                0 + speed
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