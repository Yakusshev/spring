package com.yakushev.spring.feature.test.domain

import com.yakushev.spring.feature.test.data.TestRepository
import javax.inject.Inject

class IsDisplaySnakeLengthUseCase @Inject constructor(
    private val repository: TestRepository
) {
    operator fun invoke(): Boolean = repository.isDisplaySnakeLength()
}