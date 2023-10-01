package com.yakushev.spring.domain.model

enum class Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT;

    fun opposite(): Direction =
        when (this) {
            UP -> DOWN
            DOWN -> UP
            RIGHT -> LEFT
            LEFT -> RIGHT
        }
}