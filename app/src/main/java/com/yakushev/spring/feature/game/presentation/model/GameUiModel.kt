package com.yakushev.spring.feature.game.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Path
import com.yakushev.spring.feature.game.domain.model.ApplePointModel

@Immutable
data class GameUiModel(
    val snakePathList: List<Path>,
    val snakeWidth: Float,
    val appleList: List<ApplePointModel>,
    val borderPathList: List<Path>,
)
