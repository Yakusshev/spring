package com.yakushev.spring.domain.model

enum class DirectionEnum {
    UP,
    DOWN,
    RIGHT,
    LEFT;

    fun opposite(): DirectionEnum =
        when (this) {
            UP -> DOWN
            DOWN -> UP
            RIGHT -> LEFT
            LEFT -> RIGHT
        }
}