package com.yakushev.spring.feature.game.data

import androidx.compose.ui.graphics.Path
import com.yakushev.spring.feature.game.domain.model.ApplePointModel
import com.yakushev.spring.feature.game.domain.model.Point

typealias PathModel = List<List<Point>>

fun level1(
    height: Float,
    width: Float,
    cellHalfWidth: Float,
): List<List<Point>> =
    listOf(
        listOf(
            ApplePointModel(x = 0f + cellHalfWidth, y = 0f + cellHalfWidth),
            ApplePointModel(x = 0f + cellHalfWidth, y = height - cellHalfWidth),
            ApplePointModel(x = width - cellHalfWidth, y = height - cellHalfWidth),
            ApplePointModel(x = width - cellHalfWidth, y = 0f + cellHalfWidth),
        )
    )


fun asd() {
    Path()
}

//data class PathModel(
//    val List<List<Point>>
//)