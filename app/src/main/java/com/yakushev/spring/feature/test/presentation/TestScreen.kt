package com.yakushev.spring.feature.test.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun TestScreen(
    viewModelFactory: ViewModelProvider.Factory,
    viewModel: TestViewModel = viewModel(factory = viewModelFactory),
) {
    val sliderPosition = remember { mutableStateOf(0f) }
    val fps = viewModel.getFpsState().collectAsState().value
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, ShapeDefaults.Large)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleText(text = "Settings")
        SettingTitle("FPS", fps.toInt().toString())
        Slider(
            value = fps,
            onValueChange = viewModel::setFps,
            valueRange = TestViewModel.FPS_RANGE,
        )
        Row {
            Text(
                text = "Display snake length",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = TextUnit(16f, TextUnitType.Sp)
            )
            Switch(
                checked = viewModel.getDisplaySnakeLengthState().collectAsState().value,
                onCheckedChange = viewModel::setDisplaySnakeLength,
            )
        }
//        RangeSlider(value = 0f.rangeTo(100f), onValueChange = {})
    }
}

@Composable
private fun ColumnScope.TitleText(text: String) {
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = TextUnit(24f, TextUnitType.Sp)
    )
}

@Composable
private fun ColumnScope.SettingTitle(text: String, value: String) {
    Text(
        modifier = Modifier.align(Alignment.Start),
        text = "$text: $value",
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = TextUnit(16f, TextUnitType.Sp)
    )
}

@Composable
private fun RowScope.SettingTitle(text: String, value: String) {

}