package com.yakushev.spring.domain.model

data class SnakeState(
    val x: Int,
    val y: Int,
    val size: Int
) {
    companion object {
        val empty = SnakeState(
            x = 0,
            y = 0,
            size = 0
        )
    }
}