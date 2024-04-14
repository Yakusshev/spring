package com.yakushev.spring.feature.game.domain.loop

import com.yakushev.spring.feature.game.data.GameDataSource
import javax.inject.Inject

internal class UpdateSnakeLengthUseCase @Inject constructor(
    private val dataSource: GameDataSource,
    private val calculateSnakeLengthUseCase: CalculateSnakeLengthUseCase,
) {
    operator fun invoke() {
        dataSource.updateSnakeLength(length = calculateSnakeLengthUseCase())
    }
}