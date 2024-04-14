package com.yakushev.spring.feature.game.domain.loop

import android.util.Log
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.GameStage
import com.yakushev.spring.feature.game.domain.model.Point
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import javax.inject.Inject
import kotlin.math.abs

internal class HandleBorderCollisionScenario @Inject constructor(
    private val dataSource: GameDataSource,
) {
    suspend operator fun invoke() {
        val snake = dataSource.getSnakeState().value ?: run {
            Log.e(this::class.simpleName, "snake or snakeHead is null")
            return
        }
        val head = snake.pointList.firstOrNull() ?: run {
            Log.e(this::class.simpleName, "snake or snakeHead is null")
            return
        }

        val borders = dataSource.getBordersState().value
        val radius = snake.width / 5

        borders.forEachIndexed { k, border ->
            border.forEachIndexed { index, point ->
                val nextPoint = border.getOrNull(index = index + 1)
                Log.d("###", "${this::class.simpleName}: index = $k.$index ")
                if (nextPoint != null && (checkXCollision(head, point, nextPoint, radius)
                    || checkYCollision(head, point, nextPoint, radius))
                ) {
                    Log.d("###", "border collision: $index.$point ` ${index + 1}.$nextPoint")
                    dataSource.setGameState(GameStage.Potracheno(length = dataSource.getDebugSnakeLengthState().value.toInt()))
                    return
                }
            }
        }
    }

    private fun checkYCollision(
        head: SnakePointModel,
        point: Point,
        nextPoint: Point,
        radius: Float
    ): Boolean {
        val vertical = abs(x = point.y - nextPoint.y) < 1
        val x = head.x in point.x..nextPoint.x || head.x in nextPoint.x..point.x
        val y = head.y in point.y - radius..point.y + radius
        Log.d("###", "${this::class.simpleName}: y equals; x = $x, y = $y")
        return vertical && x && y
    }

    private fun checkXCollision(
        head: SnakePointModel,
        point: Point,
        nextPoint: Point,
        radius: Float
    ): Boolean {
        val horizontal = abs(x = point.x - nextPoint.x) < 1
        val y = head.y in point.y..nextPoint.y || head.y in nextPoint.y..point.y
        val x = head.x in point.x - radius..point.x + radius
        Log.d("###", "${this::class.simpleName}: x equals; x = $x, y = $y")
        return horizontal && x && y
    }
}
