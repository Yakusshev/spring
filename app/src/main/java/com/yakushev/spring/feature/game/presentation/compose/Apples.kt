package com.yakushev.spring.feature.game.presentation.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import com.yakushev.spring.feature.game.domain.model.ApplePointModel

fun DrawScope.neonApples(
    apples: List<ApplePointModel>,
    width: Float,
    neonPaint: Paint,
    whitePaint: Paint,
) {
    drawIntoCanvas { canvas ->
        apples.forEach { apple ->
            canvas.drawCircle(
                center = Offset(apple.x, apple.y),
                radius = width / 3f,
                paint = neonPaint
            )
            canvas.drawCircle(
                center = Offset(apple.x, apple.y),
                radius = width / 3f,
                paint = whitePaint
            )
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
                topLeft = Offset(apple.x, apple.y),
                style = Stroke(
                    width = width.toFloat(),
                    cap = StrokeCap.Square
                )
            )
        }
    }
}