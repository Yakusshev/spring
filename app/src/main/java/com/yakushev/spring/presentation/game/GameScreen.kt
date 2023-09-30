package com.yakushev.spring.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
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
    LocalConfiguration.current.run {
        viewModel.onInitScreen(screenWidthDp, screenHeightDp)
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
    Box(
        modifier = Modifier
            .size(snake.size.dp)
            .offset(x = snake.x.dp, y = snake.y.dp)
            .background(color = MaterialTheme.colorScheme.primary)
    )
}

@Preview
@Composable
fun PreviewMainMenuScreen() {

}