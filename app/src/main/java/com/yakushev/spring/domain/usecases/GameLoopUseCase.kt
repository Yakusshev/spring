package com.yakushev.spring.domain.usecases

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.GameConstants.SNAKE_SPEED
import com.yakushev.spring.domain.model.Direction
import com.yakushev.spring.domain.model.SnakeState
import javax.inject.Inject

class GameLoopUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        val direction = dataSource.getDirectionState().value

        dataSource.updateSnakeState { snake ->
            when (direction) {
                Direction.UP -> snake.up()
                Direction.DOWN -> snake.down()
                Direction.RIGHT -> snake.right()
                Direction.LEFT -> snake.left()
            }
        }
    }

    private fun SnakeState.right(): SnakeState {
        return copy(
            x = if (x < dataSource.getFieldWidth()) x + SNAKE_SPEED else -size
        )
    }

    private fun SnakeState.left(): SnakeState {
        return copy(
            x = if (x > 0) x - SNAKE_SPEED else dataSource.getFieldWidth()
        )
    }

    private fun SnakeState.up(): SnakeState {
        return copy(
            y = if (y > 0) y - SNAKE_SPEED else dataSource.getFieldHeight()
        )
    }

    private fun SnakeState.down(): SnakeState {
        return copy(
            y = if (y < dataSource.getFieldHeight()) y + SNAKE_SPEED else -size
        )
    }

}