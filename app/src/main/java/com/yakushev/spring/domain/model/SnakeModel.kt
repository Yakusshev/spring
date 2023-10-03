package com.yakushev.spring.domain.model

import com.yakushev.spring.utils.print

data class SnakeModel(
    val width: Float,
    val length: Float,
    val pointList: List<SnakePointModel>,
) {
    override fun toString(): String {
        return "width = $width, pointList(size = ${pointList.size}): ${pointList.print()}"
    }

    companion object {
        val empty = SnakeModel(
            width = 0f,
            length = 0f,
            pointList = emptyList()
        )
    }
}
