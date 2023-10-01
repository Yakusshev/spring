package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.GameState
import javax.inject.Inject

class SetPlayStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    suspend operator fun invoke(play: GameState) {
        gameDataSource.setGameState(play)
    }
}