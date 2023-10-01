package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.ApplePointModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetAppleListStateUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    operator fun invoke(): StateFlow<List<ApplePointModel>> =
        dataSource.getAppleListState()
}