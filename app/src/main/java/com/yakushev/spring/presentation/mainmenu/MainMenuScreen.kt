package com.yakushev.spring.presentation.mainmenu

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun MainMenuScreen(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: MainMenuViewModel = viewModel(factory = viewModelFactory),
    playClick: () -> Unit,
    exitClick: () -> Unit //todo
) {
    val play = viewModel.getPlayState().collectAsState().value

    (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0f)

    BackHandler(enabled = !play, onBack = exitClick)

    Menu(play, viewModel::onPlayClicked)
}

@Composable
fun Menu(play: Boolean, playClick: () -> Unit) {
    if (play) return
    var startAnimate by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimate) 1f else 0f,
        animationSpec =  tween(durationMillis = 3000),
        label = ""
    )
    LaunchedEffect(key1 = true) {
        startAnimate = true
        delay(4000)
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