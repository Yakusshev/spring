package com.yakushev.spring.domain.model

data class SnakePointModel(
    val x: Int,
    val y: Int,
    val edge: EdgeEnum
) {
    companion object {
        val empty = SnakePointModel(
            x = 0,
            y = 0,
            edge = EdgeEnum.EMPTY
        )
    }
}