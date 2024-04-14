package com.yakushev.spring.feature.game.domain.model

import java.text.DecimalFormat

data class SnakePointModel(
    override val x: Float,
    override val y: Float,
    val vx: Float,
    val vy: Float,
    val edge: EdgeEnum,
) : Point {
    override fun toString(): String {
        val dec = DecimalFormat("0000.00")
        return "(${dec.format(x)}, ${dec.format(y)}); ${getDirection()}; $edge"
    }

    fun getDirection(): DirectionEnum =
        when {
            vy < 0 -> DirectionEnum.UP
            vy > 0 -> DirectionEnum.DOWN
            vx > 0 -> DirectionEnum.RIGHT
            vx < 0 -> DirectionEnum.LEFT
            else -> DirectionEnum.STOP
        }

    companion object {
        //TODO delete
        val empty = SnakePointModel(
            x = 0f,
            y = 0f,
            vx = 0f,
            vy = 0f,
            edge = EdgeEnum.EMPTY,
        )
    }
}