package com.yakushev.spring.domain.loop

import android.util.Log
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.GameState
import javax.inject.Inject

class HandleSnakeCollisionScenario @Inject constructor(
    private val dataSource: GameDataSource,
) {
    private var size = 0
    suspend operator fun invoke() {
        val snake = dataSource.getSnakeState().value
        if (snake.pointList.size != size) {
            size = snake.pointList.size
            Log.d("###", "invoke: size $size")
        }
        if (snake.pointList.filter { point -> point.edge == EdgeEnum.EMPTY }.size < 4) return
        val radius = snake.width / 2
        val head = snake.pointList.first()

        snake.pointList.forEachIndexed { index, point ->
            if (index < 3) return@forEachIndexed
            if (index == snake.pointList.lastIndex) return
            val nextPoint = snake.pointList[index + 1]
            Log.d("###", "head: ${head.x}, ${head.y}")
            Log.d("###", "poi1: ${point.x}, ${point.y}")
            Log.d("###", "poi2: ${nextPoint.x}, ${nextPoint.y}")
            val collision = when {
                point.x == nextPoint.x -> {
                    val y = head.y in point.y..nextPoint.y || head.y in nextPoint.y..point.y
                    val x = head.x in point.x - radius..point.x + radius
                    Log.d("###", "point.x == nextPoint.x: x = $x, y = $y")
                    x && y
                }
                point.y == nextPoint.y -> {
                    val x = head.x in point.x..nextPoint.x || head.x in nextPoint.x..point.x
                    val y = head.y in point.y - radius..point.y + radius
                    Log.d("###", "point.y == nextPoint.y: x = $x, y = $y")
                    x && y
                }
                else -> return@forEachIndexed
            }
            if (collision) {
                dataSource.setGameState(GameState.Potracheno(length = dataSource.getSnakeLengthState().value.toInt()))
                return
            }
        }
    }
}