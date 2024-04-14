package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.feature.game.data.GameDataSource
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class GetSnakeLengthUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<Float> =
        gameDataSource.getDebugSnakeLengthState()
}