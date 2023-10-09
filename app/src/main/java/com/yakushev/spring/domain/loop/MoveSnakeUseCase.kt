package com.yakushev.spring.domain.loop

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import com.yakushev.spring.utils.toText
import javax.inject.Inject
import kotlin.math.sqrt

class MoveSnakeUseCase @Inject constructor(
    private val dataSource: GameDataSource,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
) {

    suspend operator fun invoke(deviation: Float) {
        updateSnakeLengthUseCase()
        dataSource.updateSnakeState { snake ->
            snake.copy(
                pointList = snake.pointList
                    .apply { Log.d("###", "MoveSnakeUseCase: {${this.toText()}") }
                    .toMutableList()
                    .move(deviation)
                    .removeCornerIfNeed()
            )
        }
    }

    private fun MutableList<SnakePointModel>.move(
        deviation: Float
    ): MutableList<SnakePointModel> {
        val appleCoef = sqrt(dataSource.getAppleEatenState().value.toFloat())
        this[0] = this[0].move(list = this, head =  true, appleCoef).first
        val tailPoint = this[lastIndex].move(list = this, head = false, appleCoef)
        if (tailPoint.second) removeEdgePoints()
        this[lastIndex] = tailPoint.first
        return this
    }

    private fun SnakePointModel.move(
        list: MutableList<SnakePointModel>,
        head: Boolean,
        deviation: Float
    ): Pair<SnakePointModel, Boolean> {
        val width = dataSource.getFieldWidth()
        val height = dataSource.getFieldHeight()
        var removeEdgePoints = false
        val newX = when {
            x < 0 -> {
                if (head) list.addEdgePoints(this, this.copy(x = width))
                else removeEdgePoints = true
                width
            }
            x > width -> {
                if (head) list.addEdgePoints(this, this.copy(x = 0f))
                else removeEdgePoints = true
                0f
            }
            else -> x + vx.pl(deviation)
        }
        val newY = when {
            y < 0 -> {
                if (head) list.addEdgePoints(this, this.copy(y = height))
                else removeEdgePoints = true
                height
            }
            y > height -> {
                if (head) list.addEdgePoints(this, this.copy(y = 0f))
                else removeEdgePoints = true
                0f
            }
            else -> y + vy.pl(deviation)
        }
        return copy(x = newX, y = newY) to removeEdgePoints
    }

    private fun Float.pl(deviation: Float): Float =
        when {
            this > 0 -> this + deviation
            this < 0 -> this - deviation
            else -> this
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
        if (size < 2) return this
        val tailPoint = this[lastIndex]
        val preTailPoint = this[lastIndex - 1]//.also { point -> if (point.edge == EdgeEnum.INPUT || point.edge == EdgeEnum.OUTPUT) return this }
        if (preTailPoint.edge == EdgeEnum.INPUT) return this

        val deviation = SNAKE_SPEED.pl(dataSource.getAppleEatenState().value.toFloat()) / 4
        val removePoint = when (tailPoint.getDirection()) {
            DirectionEnum.UP -> tailPoint.y <= preTailPoint.y + deviation
            DirectionEnum.DOWN -> tailPoint.y >= preTailPoint.y - deviation
            DirectionEnum.RIGHT -> tailPoint.x >= preTailPoint.x - deviation
            DirectionEnum.LEFT -> tailPoint.x <= preTailPoint.x + deviation
            DirectionEnum.STOP -> false
        }

        if (removePoint) {
            Log.d("###", "MoveSnakeUseCase removeCornerIfNeed: tail$tailPoint ` preTail$preTailPoint")
            remove(tailPoint)
        }
        return this
    }

    private fun MutableList<SnakePointModel>.removeEdgePoints(): MutableList<SnakePointModel> {
        Log.d("###", "MoveSnakeUseCase removeEdgePoints: ${this.toText()}")
        findLast { point -> point.edge == EdgeEnum.OUTPUT }?.let { point -> remove(point) }
        findLast { point -> point.edge == EdgeEnum.INPUT }?.let { point -> remove(point) }
        Log.d("###", "MoveSnakeUseCase removeEdgePoints: ${this.toText()}")
        return this
    }

    companion object {
    }
}