package com.yakushev.spring.presentation.game.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.presentation.game.GameViewModel

//TODO A separate library, androidx.compose.material:material-icons-extended, contains the full set of Material icons. Due to the very large size of this library, make sure to use R8/Proguard to strip unused icons if you are including this library as a direct dependency. Alternatively you can make a local copy (by copy and pasting) the icon(s) you wish to keep, or using Android Studio's 'Import vector asset' feature.

@Composable
fun Menu(viewModel: GameViewModel) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        PlayButton(viewModel)
        PauseButton(viewModel)
        Potracheno(viewModel)
    }
}

@Composable
private fun BoxWithConstraintsScope.PauseButton(viewModel: GameViewModel) {
    val state = viewModel.getGameState().collectAsState().value == GameState.Play
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopEnd),
        visible = state
    ) {
        Icon(
            modifier = Modifier
                .size(96.dp)
                .padding(16.dp)
                .clickable(enabled = state, onClick = viewModel::onPauseClicked),
            imageVector = Icons.Default.Pause,
            contentDescription = "pause",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.PlayButton(viewModel: GameViewModel) {
    val state = viewModel.getGameState().collectAsState().value == GameState.Pause
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = state
    ) {
        Icon(
            modifier = Modifier
                .size(128.dp)
                .clickable(enabled = state, onClick = viewModel::onPlayClicked),
            imageVector = Icons.Default.PlayArrow,
            contentDescription = "play",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun BoxWithConstraintsScope.Potracheno(viewModel: GameViewModel) {
    val state = viewModel.getGameState().collectAsState().value is GameState.Potracheno
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = state
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PotrachenoText(text = "ПОТРАЧЕНО")
            Icon(
                modifier = Modifier
                    .size(128.dp)
                    .clickable(enabled = state, onClick = viewModel::onResetClicked),
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