package com.yakushev.spring.domain.model

enum class EdgeEnum {
    EMPTY,
    INPUT,
    OUTPUT;

    override fun toString(): String =
        when (this) {
            EMPTY -> "E"
            INPUT -> "I"
            OUTPUT -> "O"
        }
}