package com.yakushev.spring.presentation.mainmenu

import androidx.lifecycle.ViewModel
import com.yakushev.spring.domain.model.GameState
import com.yakushev.spring.domain.usecases.GetPlayStateUseCase
import com.yakushev.spring.domain.usecases.SetPlayStateUseCase
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MainMenuViewModel @Inject constructor(
    private val setPlayStateUseCase: SetPlayStateUseCase,
    private val getPlayStateUseCase: GetPlayStateUseCase
) : ViewModel() {
    internal fun getPlayState(): StateFlow<GameState> = getPlayStateUseCase()
}