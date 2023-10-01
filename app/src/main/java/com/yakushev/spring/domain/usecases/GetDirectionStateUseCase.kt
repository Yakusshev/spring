package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.DirectionEnum
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetDirectionStateUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<DirectionEnum> =
        gameDataSource.getDirectionState()
}