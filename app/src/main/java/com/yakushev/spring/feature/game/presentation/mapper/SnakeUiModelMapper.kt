package com.yakushev.spring.feature.game.presentation.mapper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.yakushev.spring.feature.game.domain.model.EdgeEnum
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import com.yakushev.spring.feature.game.domain.model.SnakePointModel
import com.yakushev.spring.feature.game.presentation.model.SnakeUiModel

internal fun SnakeModel.toSnakeUiModel(): SnakeUiModel =
    SnakeUiModel(
        pointList = pointList.map { point -> Offset(point.x, point.y) },
        pathList = pointList.getPathList(),
        width = width
    )


private fun List<SnakePointModel>.getPathList(): List<Path> {
    val pathList = mutableListOf<Path>()

    forEachIndexed { index, point ->
        when {
            index == 0 -> {
                pathList.add(Path().apply { reset() })
                pathList.last().moveTo(point.x, point.y)
            }
            pathList.last().isEmpty -> {
                pathList.last().moveTo(point.x.toFloat(), point.y.toFloat())
            }
            else -> {
                pathList.last().lineTo(point.x.toFloat(), point.y.toFloat())
                if (point.edge == EdgeEnum.OUTPUT || point.edge == EdgeEnum.INPUT) {
                    pathList.add(Path().apply { reset() })
                }
            }
        }
    }

    return pathList
}
