package com.yakushev.spring.feature.game.presentation.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb

internal fun DrawScope.neonSnake(
    pathList: List<Path>,
    neonPaint: Paint,
    whitePaint: Paint,
) {
    drawIntoCanvas { canvas ->
        pathList.forEach { currentPath ->
            canvas.drawPath(
                path = currentPath,
                paint = neonPaint
            )
            canvas.drawPath(
                path = currentPath,
                paint = whitePaint
            )
        }
    }
}

internal fun getWhitePaint(width: Float, onSurface: Color, style: PaintingStyle): Paint =
    Paint().apply {
        this.style = style
        strokeWidth = width / 2f
        color = onSurface
        strokeCap = StrokeCap.Round
        pathEffect = PathEffect.cornerPathEffect(width)
    }

internal fun getNeonPaint(width: Float, alpha: Float, neonColor: Color): Paint =
    Paint().apply {
        style = PaintingStyle.Stroke
        strokeWidth = width
        strokeCap = StrokeCap.Round
        pathEffect = PathEffect.cornerPathEffect(width)
        color = Color.Transparent
        asFrameworkPaint().apply {
            setShadowLayer(
                width,
                0f,
                0f,
                neonColor.copy(alpha = alpha).toArgb()
            )
        }
    }