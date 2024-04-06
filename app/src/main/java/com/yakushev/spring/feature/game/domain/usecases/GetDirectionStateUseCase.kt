package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.DirectionEnum
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

internal class GetDirectionStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<DirectionEnum> =
        gameDataSource.getDirectionState()
}