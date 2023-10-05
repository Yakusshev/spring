package com.yakushev.spring.domain.model

import com.yakushev.spring.domain.Const
import java.text.DecimalFormat

data class SnakePointModel(
    val x: Float,
    val y: Float,
    val edge: EdgeEnum,
    val vx: Float,
    val vy: Float,
) {
    override fun toString(): String {
        val dec = DecimalFormat("0000.00")
        return "x = ${dec.format(x)}, y = ${dec.format(y)}, $direction, $edge"
    }

    companion object {
        val empty = SnakePointModel(
            x = 0f,
            y = 0f,
            edge = EdgeEnum.EMPTY,
            vx = 0f,
            vy = 0f,
        )
    }
}