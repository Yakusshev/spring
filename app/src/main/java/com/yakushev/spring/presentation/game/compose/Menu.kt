package com.yakushev.spring.presentation.game.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.presentation.game.GameViewModel
import kotlinx.coroutines.delay


@Composable
fun Menu(viewModel: GameViewModel) {
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

    when (viewModel.getGameState().collectAsState().value) {
        GameState.Pause -> PlayButton(alpha = alphaAnimation.value, playClick = viewModel::onPlayClicked)
        GameState.Play -> {}
        is GameState.Potracheno -> Potracheno(alpha = alphaAnimation.value, onClick = viewModel::onResetClicked)
    }
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
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun Potracheno(alpha: Float, onClick: () -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PotrachenoText(text = "ПОТРАЧЕНО")
            Icon(
                modifier = Modifier
                    .size(128.dp)
                    .alpha(alpha = alpha)
                    .clickable(onClick = onClick),
                imageVector = Icons.Default.Refresh,
                contentDescription = "refresh",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun PotrachenoText(text: String) {
    Text(
        modifier = Modifier
            .padding(0.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight(1000),
        fontSize = TextUnit(48f, TextUnitType.Sp)
    )
}