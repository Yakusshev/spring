package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.GameConstants
import kotlinx.coroutines.delay
import javax.inject.Inject

class GameLoopUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        dataSource.getPlayState().collect { playState ->
            while (playState) {
                step()
                delay(GameConstants.DELAY)
            }
        }
    }

    private fun step() {
        dataSource.updateSnakeState { snake ->
            var newX = snake.x + 1
            if (newX + snake.size > dataSource.getFieldWidth()) newX = 0
            snake.copy(x = newX)
        }
    }
}