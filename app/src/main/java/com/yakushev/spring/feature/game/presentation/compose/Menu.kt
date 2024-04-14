package com.yakushev.spring.feature.game.presentation.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material.icons.sharp.Settings
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
import androidx.navigation.NavController
import com.yakushev.spring.feature.game.domain.model.GameStage
import com.yakushev.spring.feature.game.presentation.GameViewModel
import com.yakushev.spring.navigation.Route

//TODO A separate library, androidx.compose.material:material-icons-extended, contains the full set of Material icons. Due to the very large size of this library, make sure to use R8/Proguard to strip unused icons if you are including this library as a direct dependency. Alternatively you can make a local copy (by copy and pasting) the icon(s) you wish to keep, or using Android Studio's 'Import vector asset' feature.

private val iconSize = 48.dp

@Composable
internal fun Menu(viewModel: GameViewModel, navController: NavController) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        PlayButton(viewModel)
        PauseButton(viewModel)
        SettingsButton(viewModel, navController)
        Potracheno(viewModel)
    }
}

@Composable
private fun BoxWithConstraintsScope.PauseButton(viewModel: GameViewModel) {
    val state = viewModel.getGameStage().collectAsState().value == GameStage.Play
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopEnd),
        visible = state,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .size(iconSize)
                .clickable(enabled = state, onClick = viewModel::onPauseClicked),
            imageVector = Icons.Default.Pause,
            contentDescription = "pause",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.SettingsButton(
    viewModel: GameViewModel,
    navController: NavController
) {
    val state = viewModel.getGameStage().collectAsState().value == GameStage.Pause
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.TopEnd),
        visible = state,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Icon(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .size(iconSize)
                .clickable(enabled = state) { navController.navigate(Route.SETTINGS) },
            imageVector = Icons.Sharp.Settings,
            contentDescription = "pause",
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.PlayButton(viewModel: GameViewModel) {
    val state = viewModel.getGameStage().collectAsState().value == GameStage.Pause
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = state,
        enter = fadeIn(),
        exit = fadeOut()
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun BoxWithConstraintsScope.Potracheno(viewModel: GameViewModel) {
    val state = viewModel.getGameStage().collectAsState().value is GameStage.Potracheno
    AnimatedVisibility(
        modifier = Modifier.align(Alignment.Center),
        visible = state,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PotrachenoText(text = "GAME OVER")
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
        modifier = Modifier.padding(0.dp),
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight(1000),
        fontSize = TextUnit(48f, TextUnitType.Sp)
    )
}