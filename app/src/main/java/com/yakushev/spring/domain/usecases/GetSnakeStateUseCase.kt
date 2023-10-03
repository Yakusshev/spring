package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.SnakeModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSnakeStateUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<SnakeModel> =
        dataSource.getSnakeState()
}