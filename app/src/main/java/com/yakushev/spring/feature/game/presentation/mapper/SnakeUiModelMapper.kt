package com.yakushev.spring.feature.game.presentation.mapper

import androidx.compose.ui.graphics.Path
import com.yakushev.spring.feature.game.domain.model.EdgeEnum
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import com.yakushev.spring.feature.game.presentation.model.SnakeUiModel

internal fun SnakeModel.toSnakeUiModel(): SnakeUiModel =
    SnakeUiModel(
        pathList = pointList.toPathList(),
        width = width
    )


internal fun List<SnakePointModel>.toPathList(): List<Path> {
    val pathList = mutableListOf<Path>()

    forEachIndexed { index, point ->
        when {
            index == 0 -> {
                pathList.add(Path())
                pathList.last().moveTo(point.x, point.y)
            }
            point.edge == EdgeEnum.INPUT -> {
                pathList.last().moveTo(point.x, point.y)
            }
            point.edge == EdgeEnum.OUTPUT -> {
                pathList.last().lineTo(point.x, point.y)
                pathList.add(Path())
            }
            else -> {
                pathList.last().lineTo(point.x, point.y)
            }
        }
    }

    return pathList
}
