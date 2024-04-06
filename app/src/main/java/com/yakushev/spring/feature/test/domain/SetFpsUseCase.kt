package com.yakushev.spring.feature.test.domain

import com.yakushev.spring.feature.test.data.TestRepository
import javax.inject.Inject

class SetFpsUseCase @Inject constructor(
    private val repository: TestRepository
) {
    operator fun invoke(fps: Float) {
        repository.setDelay(1000 / fps)
    }
}