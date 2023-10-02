package com.yakushev.spring.presentation.game.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.yakushev.spring.domain.model.ApplePointModel

@Composable
fun NeonApples(apples: List<ApplePointModel>, width: Int) {
    val appleColor = Color.Red
    val onSurface = MaterialTheme.colorScheme.onSurface
    val neonPaint = remember(width) { getNeonPaint(width, 1f, appleColor) }
    val whitePaint =  remember(width) { getWhitePaint(width, onSurface, PaintingStyle.Fill) }
    Canvas(modifier = Modifier.fillMaxSize()) {
        apples.forEach { apple ->
            drawIntoCanvas { canvas ->
                canvas.drawCircle(
                    center = Offset(apple.x.toFloat(), apple.y.toFloat()),
                    radius = width / 3f,
                    paint = neonPaint
                )
                canvas.drawCircle(
                    center = Offset(apple.x.toFloat(), apple.y.toFloat()),
                    radius = width / 3f,
                    paint = whitePaint
                )
            }
//            drawRect(
//                color = appleColor,
//                size = Size(1f, 1f),
//                topLeft = Offset(apple.x.toFloat(), apple.y.toFloat()),
//                style = Stroke(
//                    width = width.toFloat(),
//                    cap = StrokeCap.Square
//                )
//            )
        }
    }
}

@Composable
internal fun Apples(apples: List<ApplePointModel>, width: Int) {
    val appleColor = MaterialTheme.colorScheme.error
    Canvas(modifier = Modifier.fillMaxSize()) {
        apples.forEach { apple ->
            drawRect(
                color = appleColor,
                size = Size(1f, 1f),
                topLeft = Offset(apple.x.toFloat(), apple.y.toFloat()),
                style = Stroke(
                    width = width.toFloat(),
                    cap = StrokeCap.Square
                )
            )
        }
    }
}