package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSnakeLengthUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<Int> =
        gameDataSource.getSnakeLengthState()
}