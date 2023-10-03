package com.yakushev.spring.domain.model

data class SnakePointModel(
    val x: Float,
    val y: Float,
    val edge: EdgeEnum
) {
    override fun toString(): String {
        return "x = $x, y = $y, edge = $edge"
    }

    companion object {
        val empty = SnakePointModel(
            x = 0f,
            y = 0f,
            edge = EdgeEnum.EMPTY
        )
    }
}