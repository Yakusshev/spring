package com.yakushev.spring.feature.test.domain

import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.test.data.TestRepository
import javax.inject.Inject

internal class SetDisplaySnakeLengthUseCase @Inject constructor(
    private val repository: TestRepository,
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(display: Boolean) {
        repository.setDisplaySnakeLength(display)
        gameDataSource.updateDisplaySnakeLength(repository.isDisplaySnakeLength())
    }
}