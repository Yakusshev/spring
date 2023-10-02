package com.yakushev.spring.presentation.game.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

data class SnakeUiModel(
    val pointList: List<Offset>,
    val pathList: List<Path>,
    val width: Float
)