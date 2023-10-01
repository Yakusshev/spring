package com.yakushev.spring.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.SnakeState
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
    val snakeState = viewModel.getSnakeState().collectAsState().value
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val (x, y) = dragAmount
                    val threshold = 5
                    if (abs(x) < threshold && abs(y) < threshold) return@detectDragGestures
                    if (abs(x) > abs(y)) {
                        if (x > 0) viewModel.onDirectionChanged(Direction.RIGHT)
                        else viewModel.onDirectionChanged(Direction.LEFT)
                    } else {
                        if (y > 0) viewModel.onDirectionChanged(Direction.DOWN)
                        else viewModel.onDirectionChanged(Direction.UP)
                    }
                }
            }
    ) {
        Snake(snakeState)
    }
}


@Composable
private fun Snake(snake: SnakeState) {
    val snakeColor = MaterialTheme.colorScheme.primary

    val points = snake.pointList.map { point ->
        Offset(point.x.toFloat(), point.y.toFloat())
    }

    val path = remember { Path() }
    path.reset()

    points.forEachIndexed { index, point ->
        if (index == 0) path.moveTo(point.x, point.y)
        else path.lineTo(point.x, point.y)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPath(
            color = snakeColor,
            path = path,
            style = Stroke(
                width = snake.width.toFloat(),
//                miter = 0.01f
                cap = StrokeCap.Square
            )
        )
    }
}

@Composable
private fun SnakeOld(snake: SnakeState) {
    Box(
        modifier = Modifier
            .size(snake.width.dp)
//            .offset(x = snake.x.dp, y = snake.y.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    )
}

@Preview
@Composable
fun PreviewMainMenuScreen() {

}