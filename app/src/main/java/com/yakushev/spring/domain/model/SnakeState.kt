package com.yakushev.spring.domain.model

data class SnakeState(
    val width: Int,
    val pointList: List<Point>,
) {
    companion object {
        val empty = SnakeState(
            width = 0,
            pointList = emptyList()
        )
    }
}

data class Point(
    val x: Int,
    val y: Int
) {
    companion object {
        val empty = Point(
            x = 0,
            y = 0
        )
    }
}