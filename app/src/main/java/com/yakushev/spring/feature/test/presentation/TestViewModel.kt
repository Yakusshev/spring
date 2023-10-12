package com.yakushev.spring.feature.test.presentation

import androidx.lifecycle.ViewModel
import com.yakushev.spring.feature.test.domain.GetFpsUseCase
import com.yakushev.spring.feature.test.domain.SetFpsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class TestViewModel @Inject constructor(
    private val getFpsUseCase: GetFpsUseCase,
    private val setFpsUseCase: SetFpsUseCase,
) : ViewModel() {
    private val fpsState = MutableStateFlow(getFpsUseCase())

    internal fun getFpsState(): StateFlow<Float> = fpsState

    internal fun setFps(fps: Float) {
        setFpsUseCase(fps)
        fpsState.update { getFpsUseCase() }
    }

    companion object {
        val FPS_RANGE = 1f..125f
    }
}