package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.SnakeModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class GetSnakeStateUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<SnakeModel?> = dataSource.getSnakeState()
}