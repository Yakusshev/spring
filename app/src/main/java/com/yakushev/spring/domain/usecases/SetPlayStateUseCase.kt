package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import javax.inject.Inject

class SetPlayStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    suspend operator fun invoke(play: Boolean) {
        gameDataSource.setPlay(play)
    }
}