package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import javax.inject.Inject

class GameLoopUseCase @Inject constructor(
    dataSource: GameDataSource
) {
    suspend operator fun invoke() {

    }
}