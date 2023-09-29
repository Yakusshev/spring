package com.yakushev.spring.presentation.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.SnakeState
import kotlinx.coroutines.delay

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
    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Snake(snakeState)
        Buttons(viewModel)
    }
}

@Composable
private fun BoxWithConstraintsScope.Buttons(viewModel: GameViewModel) {
    ConstraintLayout(
        modifier = Modifier.align(Alignment.BottomCenter)
    ) {
        val (up, down, right, left) = createRefs()

        val margin = 64.dp

        Box(
            Modifier
                .constrainAs(down) {
                    bottom.linkTo(parent.bottom, margin)
                    end.linkTo(parent.end)
                }
                .directionButtonModifier(viewModel, Direction.DOWN)
        )
        Box(
            Modifier
                .constrainAs(up) {
                    bottom.linkTo(down.top, margin)
                    end.linkTo(parent.end)
                }
                .directionButtonModifier(viewModel, Direction.UP)
        )
        Box(
            Modifier
                .constrainAs(right) {
                    bottom.linkTo(down.top)
                    start.linkTo(left.end, margin)
                }
                .directionButtonModifier(viewModel, Direction.RIGHT)
        )
        Box(
            modifier = Modifier
                .constrainAs(left) {
                    bottom.linkTo(down.top)
                    end.linkTo(down.start)
                }
                .directionButtonModifier(viewModel, Direction.LEFT)
        )
    }
}

fun Modifier.directionButtonModifier(
    viewModel: GameViewModel,
    direction: Direction
): Modifier = this
    .rotate(degrees = 45f)
    .size(64.dp)
    .alpha(0.25f)
    .background(color = Color.Red)
    .clickable { viewModel.onDirectionButtonClicked(direction) }

@Composable
private fun Snake(snake: SnakeState) {
    Box(
        modifier = Modifier
            .size(snake.size.dp)
//            .offset { IntOffset(x = state.x, y = state.y) }
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