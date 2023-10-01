package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.GameState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPlayStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<GameState> =
        gameDataSource.getGameState()
}