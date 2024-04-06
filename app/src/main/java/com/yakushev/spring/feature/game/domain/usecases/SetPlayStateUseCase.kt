package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.GameState
import javax.inject.Inject

internal class SetPlayStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    suspend operator fun invoke(play: GameState) {
        gameDataSource.setGameState(play)
    }
}