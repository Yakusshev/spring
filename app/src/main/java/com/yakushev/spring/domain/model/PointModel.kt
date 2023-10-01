package com.yakushev.spring.domain.model

data class PointModel(
    val x: Int,
    val y: Int,
    val edge: EdgeEnum
) {
    companion object {
        val empty = PointModel(
            x = 0,
            y = 0,
            edge = EdgeEnum.EMPTY
        )
    }
}