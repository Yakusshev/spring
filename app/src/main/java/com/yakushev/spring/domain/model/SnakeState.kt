package com.yakushev.spring.domain.model

data class SnakeState(
    val x: Int,
    val y: Int,
    val width: Int,
    val length: Int,
    val turnList: List<Point>,
) {
    companion object {
        val empty = SnakeState(
            width = 0,
            length = 0,
            turnList = emptyList(),
            x = 0,
            y = 0
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