package com.yakushev.spring.presentation.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yakushev.spring.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.domain.usecases.SetPlayStateUseCase
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainMenuViewModel @Inject constructor(
    private val setPlayStateUseCase: SetPlayStateUseCase,
    private val getPlayStateUseCase: GetPlayStateUseCase
) : ViewModel() {
    internal fun getPlayState(): StateFlow<Boolean> = getPlayStateUseCase()

    internal fun onPlayClicked() {
        viewModelScope.launch { setPlayStateUseCase(play = true) }
    }

    internal fun onPauseClicked() {
        viewModelScope.launch { setPlayStateUseCase(play = false) }
    }
}