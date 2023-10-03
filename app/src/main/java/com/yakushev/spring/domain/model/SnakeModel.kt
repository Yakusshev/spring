package com.yakushev.spring.domain.model

data class SnakeModel(
    val width: Float,
    val pointList: List<SnakePointModel>,
) {
    override fun toString(): String {
        return "width = $width, pointList(size = ${pointList.size}): ${pointList.print()}"
    }

    private fun List<SnakePointModel>.print(): String {
        var string = ""
        forEachIndexed { index, snakePointModel ->
            string += "$index. $snakePointModel| "
        }
        return "$string."
    }

    companion object {
        val empty = SnakeModel(
            width = 0f,
            pointList = emptyList()
        )
    }
}
