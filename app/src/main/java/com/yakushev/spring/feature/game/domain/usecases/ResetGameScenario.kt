package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.loop.GenerateApplesUseCase
import com.yakushev.spring.feature.game.domain.loop.UpdateSnakeLengthUseCase
import javax.inject.Inject

internal class ResetGameScenario @Inject constructor(
    private val dataSource: GameDataSource,
    private val updateSnakeLengthUseCase: UpdateSnakeLengthUseCase,
    private val initGameUseCase: InitGameUseCase,
    private val generateApplesUseCase: GenerateApplesUseCase,
) {
    suspend operator fun invoke() {
        initGameUseCase()
        updateSnakeLengthUseCase()
        dataSource.updateDirectionState { Const.DEFAULT_DIRECTION }
        dataSource.updateAndGetAppleEaten { 0 }
        generateApplesUseCase(reset = true)
    }
}
