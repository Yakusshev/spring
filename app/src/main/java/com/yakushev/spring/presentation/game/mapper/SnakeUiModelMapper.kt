package com.yakushev.spring.presentation.game.mapper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakeModel
import com.yakushev.spring.domain.model.SnakePointModel
import com.yakushev.spring.presentation.game.model.SnakeUiModel

internal fun SnakeModel.toSnakeUiModel(): SnakeUiModel =
    SnakeUiModel(
        pointList = pointList.map { point -> Offset(point.x.toFloat(), point.y.toFloat()) },
        pathList = pointList.getPathList(),
        width = width.toFloat()
    )


private fun List<SnakePointModel>.getPathList(): List<Path> {
    val pathList = mutableListOf<Path>()

    forEachIndexed { index, point ->
        when {
            index == 0 -> {
                pathList.add(Path().apply { reset() })
                pathList.last().moveTo(point.x.toFloat(), point.y.toFloat())
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
