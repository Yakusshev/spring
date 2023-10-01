package com.yakushev.spring.domain.model

data class SnakeModel(
    val width: Int,
    val pointList: List<PointModel>,
) {
    companion object {
        val empty = SnakeModel(
            width = 0,
            pointList = emptyList()
        )
    }
}
