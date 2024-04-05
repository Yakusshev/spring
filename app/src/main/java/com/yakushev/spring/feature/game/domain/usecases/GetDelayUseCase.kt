package com.yakushev.spring.feature.game.domain.usecases

import com.yakushev.spring.feature.test.data.TestRepository
import javax.inject.Inject

class GetDelayUseCase @Inject constructor(
    private val repository: TestRepository
) {
    operator fun invoke(): Float = repository.getDelay()
}