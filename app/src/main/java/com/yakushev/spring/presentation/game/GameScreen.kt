package com.yakushev.spring.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yakushev.spring.domain.model.ApplePointModel
import com.yakushev.spring.domain.model.DirectionEnum
import com.yakushev.spring.domain.model.EdgeEnum
import com.yakushev.spring.domain.model.SnakeModel
import kotlin.math.abs

@Composable
fun GameScreen(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: GameViewModel = viewModel(factory = viewModelFactory)
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        viewModel.onInitScreen(size.width.toInt(), size.height.toInt())
    }

    Field(viewModel)

    val play = viewModel.getPlayState().collectAsState().value
    BackHandler(enabled = play, onBack = viewModel::onPauseClicked)
    Menu(play, viewModel::onPlayClicked)
}

@Composable
fun Field(viewModel: GameViewModel) {
    val snake = viewModel.getSnakeState().collectAsState().value
    val apples = viewModel.getAppleListState().collectAsState().value
    val snakeLength = viewModel.getSnakeLengthState().collectAsState().value
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) { detectSwipes(viewModel) }
    ) {
        Apples(apples = apples, width = snake.width)
        Snake(snake)
        GameScore(snakeLength)
    }
}

@Composable
fun Apples(apples: List<ApplePointModel>, width: Int) {
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

@Composable
private fun Snake(snake: SnakeModel) {
    val snakeColor = MaterialTheme.colorScheme.primary
    val points = snake.pointList
    val pathList = mutableListOf<Path>()

    points.forEachIndexed { index, point ->
        when {
            index == 0 -> {
                pathList.add(remember { Path() }.apply { reset() })
                pathList.last().moveTo(point.x.toFloat(), point.y.toFloat())
            }
            pathList.last().isEmpty -> {
                pathList.last().moveTo(point.x.toFloat(), point.y.toFloat())
            }
            else -> {
                pathList.last().lineTo(point.x.toFloat(), point.y.toFloat())
                if (point.edge == EdgeEnum.OUTPUT || point.edge == EdgeEnum.INPUT) {
                    pathList.add(remember { Path() }.apply { reset() })
                }
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        pathList.forEach { currentPath ->
            drawPath(
                color = snakeColor,
                path = currentPath,
                style = Stroke(
                    width = snake.width.toFloat(),
//                miter = 0.01f
                    cap = StrokeCap.Square
                )
            )
        }
    }
}

@Composable
fun BoxScope.GameScore(length: Int) {
    Text(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp),
        text = length.toString(),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = TextUnit(20f, TextUnitType.Sp)
    )
}

private suspend fun PointerInputScope.detectSwipes(viewModel: GameViewModel) {
    detectDragGestures { _, dragAmount ->
        val (x, y) = dragAmount
        val threshold = 5
        if (abs(x) < threshold && abs(y) < threshold) return@detectDragGestures
        if (abs(x) > abs(y)) {
            if (x > 0) viewModel.onDirectionChanged(DirectionEnum.RIGHT)
            else viewModel.onDirectionChanged(DirectionEnum.LEFT)
        } else {
            if (y > 0) viewModel.onDirectionChanged(DirectionEnum.DOWN)
            else viewModel.onDirectionChanged(DirectionEnum.UP)
        }
    }
}

@Preview
@Composable
fun PreviewMainMenuScreen() {

}