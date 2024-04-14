package com.yakushev.spring.feature.game.presentation.model

import androidx.compose.ui.graphics.Path

data class SnakeUiModel(
    val pathList: List<Path>,
    val width: Float
) {
    companion object {
        val empty = SnakeUiModel(
            pathList = emptyList(),
            width = 0f
        )
    }
}