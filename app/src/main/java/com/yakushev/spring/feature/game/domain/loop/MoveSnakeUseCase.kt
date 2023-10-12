package com.yakushev.spring.feature.game.domain.loop

import android.util.Log
import com.yakushev.spring.core.Const.SNAKE_SPEED
import com.yakushev.spring.core.log
import com.yakushev.spring.core.toText
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.EdgeEnum
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import javax.inject.Inject
import kotlin.math.sqrt

class MoveSnakeUseCase @Inject constructor(
    private val dataSource: GameDataSource,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
    private val calculateSnakeLengthUseCase: CalculateSnakeLengthUseCase,
) {
    private var fieldWidth = 0f
    private var fieldHeight = 0f

    suspend operator fun invoke(deviation: Float) {
        updateSnakeLengthUseCase()
        fieldWidth = dataSource.getFieldWidth()
        fieldHeight = dataSource.getFieldHeight()
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

        val headResult = first().move(list = this, head = true, appleCoef, deviation)
        if (headResult.teleported) addEdgePoints(first())
        this[0] = headResult.point

        val tailResult = last().move(list = this, head = false, appleCoef, deviation)
        if (tailResult.teleported) removeEdgePoints()
        this[lastIndex] = tailResult.point

        return this
    }

    private fun SnakePointModel.move(
        list: MutableList<SnakePointModel>,
        head: Boolean,
        appleCoef: Float,
        deviation: Float
    ): MoveResult {
        val newX = when {
            x < 0 -> fieldWidth
            x > fieldWidth -> 0f
            !head -> x + list.getTailSpeed().vx
            else -> x + vx.addAppleCoef(appleCoef) * deviation
        }
        val newY = when {
            y < 0 -> fieldHeight
            y > fieldHeight -> 0f
            !head -> y + list.getTailSpeed().vy
            else -> y + vy.addAppleCoef(appleCoef) * deviation
        }
        return MoveResult(
            point = copy(x = newX, y = newY),
            teleported = (x < 0 || x > fieldWidth || y < 0 || y > fieldHeight)
        )
    }

    private fun Float.addAppleCoef(appleCoef: Float): Float =
        when {
            this > 0 -> this + appleCoef
            this < 0 -> this - appleCoef
            else -> this
        }

    private fun List<SnakePointModel>.getTailSpeed(): SnakePointModel {
        if (this.size < 2) return SnakePointModel.empty
        val preTail = this[lastIndex - 1]
        val tail = last()

        if (preTail.edge == EdgeEnum.OUTPUT) return SnakePointModel.empty

        val diff = calculateSnakeLengthUseCase() - dataSource.snakeLength

        return SnakePointModel.empty.copy(
            vx = newPointSpeed(tail.vx, diff),
            vy = newPointSpeed(tail.vy, diff),
        )
    }

    private fun newPointSpeed(speed: Float, diff: Float): Float =
        when {
            speed < 0 -> -diff
            speed == 0f -> 0f
            else -> diff
        }

    private fun MutableList<SnakePointModel>.addEdgePoints(
        input: SnakePointModel
    ) {
        val output = when {
            input.x < 0 -> input.copy(x = fieldWidth)
            input.x > fieldWidth -> input.copy(x = 0f)
            input.y < 0 -> input.copy(y = fieldHeight)
            input.y > fieldHeight -> input.copy(y = 0f)
            else -> return run { log("MoveSnakeUseCase: UNEXPECTED input coordinates") }
        }
        add(1, input.copy(edge = EdgeEnum.INPUT))
        add(1, output.copy(edge = EdgeEnum.OUTPUT))
    }

    private fun MutableList<SnakePointModel>.removeCornerIfNeed(): MutableList<SnakePointModel> {
        if (size <= 2) return this
        val tailPoint = this[lastIndex]
        val preTailPoint = this[lastIndex - 1]
        if (preTailPoint.edge == EdgeEnum.INPUT) return this

        val deviation = SNAKE_SPEED.addAppleCoef(dataSource.getAppleEatenState().value.toFloat()) / 4
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

    private fun MutableList<SnakePointModel>.removeEdgePoints() {
        Log.d("###", "MoveSnakeUseCase removeEdgePoints: ${this.toText()}")
        findLast { point -> point.edge == EdgeEnum.OUTPUT }?.let { point -> remove(point) }
        findLast { point -> point.edge == EdgeEnum.INPUT }?.let { point -> remove(point) }
        Log.d("###", "MoveSnakeUseCase removeEdgePoints: ${this.toText()}")
    }

    private class MoveResult(
        val point: SnakePointModel,
        val teleported: Boolean,
    )

    companion object {
    }
}