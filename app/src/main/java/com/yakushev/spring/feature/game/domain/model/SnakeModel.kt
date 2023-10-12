package com.yakushev.spring.feature.game.domain.model

import com.yakushev.spring.core.toText

data class SnakeModel(
    val width: Float,
    val length: Float,
    val pointList: List<SnakePointModel>,
) {
    override fun toString(): String {
        return "snake. (size = ${pointList.size}): ${pointList.toText()}"
    }

    companion object {
        val empty = SnakeModel(
            width = 0f,
            length = 0f,
            pointList = emptyList()
        )
    }
}
