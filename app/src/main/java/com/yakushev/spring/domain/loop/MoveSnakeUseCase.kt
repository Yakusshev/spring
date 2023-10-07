package com.yakushev.spring.domain.loop

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.Const.SNAKE_SPEED
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakePointModel
import com.yakushev.spring.utils.toText
import javax.inject.Inject

class MoveSnakeUseCase @Inject constructor(
    private val dataSource: GameDataSource,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
) {

    suspend operator fun invoke(deviation: Float) {
        updateSnakeLengthUseCase()
        dataSource.updateSnakeState { snake ->
            snake.copy(
                pointList = snake.pointList
                    .toMutableList()
                    .removeCornerIfNeed()
                    .move(deviation)
            )
        }
    }

    private fun MutableList<SnakePointModel>.move(
        deviation: Float
    ): MutableList<SnakePointModel> {
        this[0] = this[0].move(this, true, 1f)
        this[lastIndex] = this[lastIndex].move(this, false, 1f)
        return this
    }

    private fun SnakePointModel.move(
        list: MutableList<SnakePointModel>,
        head: Boolean,
        deviation: Float
    ): SnakePointModel {
        val width = dataSource.getFieldWidth()
        val height = dataSource.getFieldHeight()
        val newX = when {
            x < 0 -> {
                if (head) list.addEdgePoints(this, this.copy(x = width))
                width
            }
            x > width -> {
                if (head) list.addEdgePoints(this, this.copy(x = 0f))
                0f
            }
            else -> x + vx * deviation
        }
        val newY = when {
            y < 0 -> {
                if (head) list.addEdgePoints(this, this.copy(y = height))
                height
            }
            y > height -> {
                if (head) list.addEdgePoints(this, this.copy(y = 0f))
                0f
            }
            else -> y + vy * deviation
        }
        return copy(x = newX, y = newY)
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

        val deviation = SNAKE_SPEED / 4
        val removePoint = when (tailPoint.getDirection()) {
            DirectionEnum.UP -> tailPoint.y <= preTailPoint.y + deviation
            DirectionEnum.DOWN -> tailPoint.y >= preTailPoint.y - deviation
            DirectionEnum.RIGHT -> tailPoint.x >= preTailPoint.x - deviation
            DirectionEnum.LEFT -> tailPoint.x <= preTailPoint.x + deviation
            DirectionEnum.STOP -> false
        }

        when {
            removePoint && preTailPoint.edge == EdgeEnum.INPUT -> removeEdgePoints()
            removePoint -> {
                Log.d("###", "removeCornerIfNeed: tail$tailPoint ` preTail$preTailPoint")
                this[lastIndex] = tailPoint.copy(vx = preTailPoint.vx, vy = preTailPoint.vy)
                remove(preTailPoint)
            }
        }
        return this
    }

    private fun MutableList<SnakePointModel>.removeEdgePoints(): MutableList<SnakePointModel> {
        Log.d("###", "removeEdgePoints: ${this.toText()}")
        findLast { point -> point.edge == EdgeEnum.OUTPUT }?.let { point -> remove(point) }
        findLast { point -> point.edge == EdgeEnum.INPUT }?.let { point -> remove(point) }
        Log.d("###", "removeEdgePoints: ${this.toText()}")
        return this
    }

    companion object {
    }
}