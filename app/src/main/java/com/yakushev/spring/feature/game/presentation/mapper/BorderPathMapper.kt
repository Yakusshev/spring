package com.yakushev.spring.feature.game.presentation.mapper

import android.util.Log
import androidx.compose.ui.graphics.Path
import com.yakushev.spring.feature.game.domain.model.Point

fun List<List<Point>>.toMultiPath(): Path =
    Path().apply {
        forEach { list ->
            list.forEachIndexed { index, point ->
                Log.d("###", "toMultiPath: ${point}")
                if (index == 0) moveTo(point.x, point.y)
                else lineTo(point.x, point.y)
            }
        }
        Log.d("###", "toMultiPath: $this")
    }
