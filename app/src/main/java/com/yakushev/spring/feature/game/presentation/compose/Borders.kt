package com.yakushev.spring.feature.game.presentation.compose

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

fun DrawScope.borders(
    pathList: List<Path>,
    neonPaint: Paint,
    whitePaint: Paint,
) {
    drawIntoCanvas { canvas ->
        pathList.forEach { path ->
            canvas.drawPath(path, neonPaint)
            canvas.drawPath(path, whitePaint)
        }
    }
}