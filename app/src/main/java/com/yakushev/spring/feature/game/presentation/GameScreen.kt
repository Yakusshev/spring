package com.yakushev.spring.feature.game.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import com.yakushev.spring.feature.game.domain.model.GameStage
import com.yakushev.spring.feature.game.presentation.compose.Menu
import com.yakushev.spring.feature.game.presentation.compose.borders
import com.yakushev.spring.feature.game.presentation.compose.getNeonPaint
import com.yakushev.spring.feature.game.presentation.compose.getWhitePaint
import com.yakushev.spring.feature.game.presentation.compose.neonApples
import com.yakushev.spring.feature.game.presentation.compose.neonSnake
import com.yakushev.spring.feature.game.presentation.model.GameUiModel
import kotlin.math.abs

@Composable
internal fun GameScreen(
    viewModelFactory: ViewModelProvider.Factory,
    navController: NavController,
    viewModel: GameViewModel = viewModel(factory = viewModelFactory),
) {

    val config = LocalConfiguration.current
    LocalDensity.current.run {
        viewModel.onInitScreen(
            width = config.screenWidthDp.dp.toPx(),
            height = config.screenHeightDp.dp.toPx(),
        )
    }

    val gameState = viewModel.getGameUiState().collectAsState().value
    gameState?.let { GameField(gameState, viewModel::onDirectionChanged) }

    GameUi(viewModel = viewModel)

    val gameStage = viewModel.getGameStage().collectAsState().value
    Menu(viewModel, navController)
    BackHandler(enabled = gameStage != GameStage.Pause, onBack = viewModel::onBackPressed)
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> viewModel.onPauseClicked()
            else -> {}
        }
    }
}

@Composable
private fun GameField(
    gameState: GameUiModel,
    onDirectionChanged: (direction: DirectionEnum) -> Unit,
) {

    val width = gameState.snakeWidth
    val onSurface = MaterialTheme.colorScheme.onSurface
    val applePaint = remember(width) { getNeonPaint(width, 1f, Color.Red) }
    val appleWhitePaint =  remember(width) { getWhitePaint(width, onSurface, PaintingStyle.Fill) }
    val borderPaint = remember(width) { getNeonPaint(width, 1f, Color.Blue) }
    val whitePaint = remember(width) { getWhitePaint(width, onSurface, PaintingStyle.Stroke) }
    val snakePaint = remember(width) { getNeonPaint(gameState.snakeWidth, 1f, Color.Green) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .pointerInput(Unit) { detectSwipes(onDirectionChanged) }
    ) {
        neonApples(apples = gameState.appleList, width = gameState.snakeWidth, applePaint, appleWhitePaint)
        neonSnake(gameState.snakePathList, snakePaint, whitePaint)
        borders(gameState.borderPathList, borderPaint, whitePaint)
    }
}

@Composable
private fun GameUi(viewModel: GameViewModel) {
    val snakeLength = viewModel.getSnakeLengthState().collectAsState().value
    val displaySnakeLength = viewModel.getDisplaySnakeLengthState().collectAsState().value
    val appleEaten = viewModel.getAppleEatenState().collectAsState().value

    Box(Modifier.fillMaxSize()) {
        AppleScoreText(Modifier.align(Alignment.TopStart), appleEaten)
        if (displaySnakeLength) {
            SnakeLengthText(
                Modifier.align(Alignment.BottomStart),
                snakeLength.toString()
            )
        }
    }
}

@Composable
private fun AppleScoreText(
    modifier: Modifier,
    appleEaten: Int
) {
    AnimatedVisibility(
        modifier = modifier.padding(24.dp),
        visible = appleEaten != 0,
        enter = fadeIn() + fadeIn(),
        exit = fadeOut() + fadeOut()
    ) {
        Text(
            text = appleEaten.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight(500),
            fontSize = TextUnit(20f, TextUnitType.Sp)
        )
    }
}

@Composable
private fun SnakeLengthText(
    modifier: Modifier,
    length: String
) {
    Text(
        modifier = modifier.padding(16.dp),
        text = length,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = TextUnit(20f, TextUnitType.Sp)
    )
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

private suspend fun PointerInputScope.detectSwipes(onDirectionChanged: (direction: DirectionEnum) -> Unit) {
    detectDragGestures { _, dragAmount ->
        val (x, y) = dragAmount
        val threshold = 8
        val range = 0.4f..0.6f
        if (abs(x) < threshold && abs(y) < threshold) return@detectDragGestures
        if (abs(x) / abs(y) in range || abs(y) / abs(x) in range) return@detectDragGestures
        if (abs(x) > abs(y)) {
            if (x > 0) onDirectionChanged(DirectionEnum.RIGHT)
            else onDirectionChanged(DirectionEnum.LEFT)
        } else {
            if (y > 0) onDirectionChanged(DirectionEnum.DOWN)
            else onDirectionChanged(DirectionEnum.UP)
        }
    }
}

@Preview
@Composable
fun PreviewMainMenuScreen() {

}