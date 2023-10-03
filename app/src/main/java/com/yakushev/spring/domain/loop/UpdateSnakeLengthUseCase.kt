package com.yakushev.spring.domain.loop

import com.yakushev.spring.data.GameDataSource
import javax.inject.Inject

class UpdateSnakeLengthUseCase @Inject constructor(
    private val dataSource: GameDataSource,
    private val calculateSnakeLengthUseCase: CalculateSnakeLengthUseCase,
) {
    suspend operator fun invoke() {
        dataSource.updateSnakeLength(length = calculateSnakeLengthUseCase() ?: -1f)
    }
}