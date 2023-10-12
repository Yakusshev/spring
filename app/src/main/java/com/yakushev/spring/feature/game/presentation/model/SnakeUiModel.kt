package com.yakushev.spring.feature.game.presentation.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

data class SnakeUiModel(
    val pointList: List<Offset>,
    val pathList: List<Path>,
    val width: Float
) {
    companion object {
        val empty = SnakeUiModel(
            pointList = emptyList(),
            pathList = emptyList(),
            width = 0f
        )
    }
}