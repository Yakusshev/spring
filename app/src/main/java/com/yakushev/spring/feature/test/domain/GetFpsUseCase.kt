package com.yakushev.spring.feature.test.domain

import com.yakushev.spring.feature.test.data.TestRepository
import javax.inject.Inject

class GetFpsUseCase @Inject constructor(
    private val repository: TestRepository
) {
    operator fun invoke(): Float = 1000 / repository.getDelay()
}