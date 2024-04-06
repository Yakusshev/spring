package com.yakushev.spring.feature.test.presentation

import androidx.lifecycle.ViewModel
import com.yakushev.spring.feature.test.domain.GetFpsUseCase
import com.yakushev.spring.feature.test.domain.IsDisplaySnakeLengthUseCase
import com.yakushev.spring.feature.test.domain.SetDisplaySnakeLengthUseCase
import com.yakushev.spring.feature.test.domain.SetFpsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TestViewModel @Inject constructor(
    private val getFpsUseCase: GetFpsUseCase,
    private val setFpsUseCase: SetFpsUseCase,
    private val isDisplaySnakeLengthUseCase: IsDisplaySnakeLengthUseCase,
    private val setDisplaySnakeLengthUseCase: SetDisplaySnakeLengthUseCase,
) : ViewModel() {
    private val fpsState = MutableStateFlow(getFpsUseCase())
    private val displaySnakeLengthState = MutableStateFlow(isDisplaySnakeLengthUseCase())

    internal fun getFpsState(): StateFlow<Float> = fpsState
    internal fun getDisplaySnakeLengthState(): StateFlow<Boolean> = displaySnakeLengthState

    internal fun setFps(fps: Float) {
        setFpsUseCase(fps)
        fpsState.update { getFpsUseCase() }
    }

    internal fun setDisplaySnakeLength(display: Boolean) {
        setDisplaySnakeLengthUseCase(display)
        displaySnakeLengthState.update { isDisplaySnakeLengthUseCase() }
    }

    companion object {
        val FPS_RANGE = 1f..125f
    }
}