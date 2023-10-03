package com.yakushev.spring.presentation.game.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import com.yakushev.spring.presentation.game.model.SnakeUiModel

@Composable
internal fun Snake(snake: SnakeUiModel) {
    val snakeColor = MaterialTheme.colorScheme.primary
    val points = snake.pointList.ifEmpty { return }

    NeonSnake(snake, points, snake.pathList)

//            drawPath(
//                brush = paint.asFrameworkPaint().,
//                path = currentPath,
//                style = Stroke(
//                    width = snake.width.toFloat(),
////                miter = 0.01f
//                    cap = StrokeCap.Square
//                ),
//                blendMode = BlendMode
//            )
//            drawPath(
//                color = Color.White,
//                path = currentPath,
//                style = Stroke(
//                    width = snake.width / 5f,
////                miter = 0.01f
//                    cap = StrokeCap.Square
//                )
//            )
}

@Composable
private fun NeonSnake(
    snake: SnakeUiModel,
    points: List<Offset>,
    pathList: List<Path>
) {
    val neonColor = Color.Green//MaterialTheme.colorScheme.primary
    val neonPaint = remember(snake.width) {
        getNeonPaint(snake.width, 1f, neonColor)
    }

    val headPaint = remember(snake.width) {
        getNeonPaint(snake.width, 0.2f, neonColor)
    }

    val onSurface = MaterialTheme.colorScheme.onSurface
    val paintWhite = remember(snake.width) {
        getWhitePaint(snake.width, onSurface, PaintingStyle.Stroke)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            canvas.drawCircle(
                center = points.first(),
                paint = headPaint,
                radius = snake.width / 2f
            )
            pathList.forEach { currentPath ->
                canvas.drawPath(
                    path = currentPath,
                    paint = neonPaint
                )
                canvas.drawPath(
                    path = currentPath,
                    paint = paintWhite
                )
            }
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