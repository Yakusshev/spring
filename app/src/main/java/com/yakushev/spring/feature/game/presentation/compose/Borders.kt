package com.yakushev.spring.feature.game.presentation.compose

import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.yakushev.spring.feature.game.domain.model.Point

fun DrawScope.borders(
    path: Path,
    neonPaint: Paint,
    whitePaint: Paint,
) {

    drawIntoCanvas { canvas ->
        canvas.drawPath(path, neonPaint)
        canvas.drawPath(path, whitePaint)
    }
}