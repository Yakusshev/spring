package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import javax.inject.Inject

class SetScreenSizeUseCase @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    operator fun invoke(width: Int, height: Int) {
        gameDataSource.setFieldSize(width, height)
    }
}