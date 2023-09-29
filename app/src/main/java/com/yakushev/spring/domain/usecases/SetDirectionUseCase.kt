package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.Direction
import javax.inject.Inject

class SetDirectionUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(direction: Direction) {
        gameDataSource.setDirection(direction)
    }
}