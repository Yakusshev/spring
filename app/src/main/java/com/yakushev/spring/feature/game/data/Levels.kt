package com.yakushev.spring.feature.game.data

import androidx.compose.ui.graphics.Path
import com.yakushev.spring.feature.game.domain.model.ApplePointModel
import com.yakushev.spring.feature.game.domain.model.Point

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

fun level2(
    height: Float,
    width: Float,
    cellHalfWidth: Float,
): List<List<Point>> =
    listOf(
        listOf(
            ApplePointModel(x = 0f + cellHalfWidth, y = height * 0.55f),
            ApplePointModel(x = 0f + cellHalfWidth, y = height - cellHalfWidth),
            ApplePointModel(x = width - cellHalfWidth, y = height - cellHalfWidth),
            ApplePointModel(x = width - cellHalfWidth, y = height * 0.55f),
        ),
        listOf(
            ApplePointModel(x = width - cellHalfWidth, y = height * 0.45f),
            ApplePointModel(x = width - cellHalfWidth, y = 0f + cellHalfWidth),
            ApplePointModel(x = 0f + cellHalfWidth, y = 0f + cellHalfWidth),
            ApplePointModel(x = 0f + cellHalfWidth, y = height * 0.45f),
        )
    )

fun level3(
    height: Float,
    width: Float,
    cellHalfWidth: Float,
): List<List<Point>> =
    listOf(
        listOf(
            ApplePointModel(x = 0f + cellHalfWidth, y = height * 0.6f),
            ApplePointModel(x = 0f + cellHalfWidth, y = height - cellHalfWidth),
            ApplePointModel(x = width - cellHalfWidth, y = height - cellHalfWidth),
            ApplePointModel(x = width - cellHalfWidth, y = height * 0.6f),
        ),
        listOf(
            ApplePointModel(x = width - cellHalfWidth, y = height * 0.4f),
            ApplePointModel(x = width - cellHalfWidth, y = 0f + cellHalfWidth),
            ApplePointModel(x = 0f + cellHalfWidth, y = 0f + cellHalfWidth),
            ApplePointModel(x = 0f + cellHalfWidth, y = height * 0.4f),
        ),
        listOf(
            ApplePointModel(x = width * .33f, y = height * .60f),
            ApplePointModel(x = width * .33f, y = height * .40f),
        ),
        listOf(
            ApplePointModel(x = width * .67f, y = height * .60f),
            ApplePointModel(x = width * .67f, y = height * .40f),
        ),
    )
