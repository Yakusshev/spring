package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.GameState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPlayStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<GameState> =
        gameDataSource.getGameState()
}