package com.yakushev.spring.domain.model

enum class DirectionEnum {
    UP,
    DOWN,
    RIGHT,
    LEFT,
    STOP;

    override fun toString(): String =
        when (this) {
            UP -> "U"
            DOWN -> "D"
            RIGHT -> "R"
            LEFT -> "L"
            STOP -> "S"
        }

//    fun toString(): String =

    fun opposite(): DirectionEnum =
        when (this) {
            UP -> DOWN
            DOWN -> UP
            RIGHT -> LEFT
            LEFT -> RIGHT
            STOP -> STOP
        }
}