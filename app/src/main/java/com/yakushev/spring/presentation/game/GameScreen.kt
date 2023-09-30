package com.yakushev.spring.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.SnakeState
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun GameScreen(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: GameViewModel = viewModel(factory = viewModelFactory)
) {
    LocalConfiguration.current.run {
        viewModel.onInitScreen(screenWidthDp, screenHeightDp)
    }
    //tk47n5tmer6r952

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
            .background(Color.Black)
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
            .background(color = Color.Green)
    )
}

@Composable
fun Menu(play: Boolean, playClick: () -> Unit) {
    if (play) return
    var startAnimate by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = ""
    )
    LaunchedEffect(key1 = true) {
        startAnimate = true
        delay(500)
    }
    PlayButton(alpha = alphaAnimation.value, playClick = playClick)
}

@Composable
fun PlayButton(alpha: Float, playClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(128.dp)
                .alpha(alpha = alpha)
                .clickable { playClick() },
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "play",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}


@Preview
@Composable
fun PreviewMainMenuScreen() {
    PlayButton(1f, {})

}