package com.yakushev.spring.data

import com.yakushev.spring.domain.GameConstants.SNAKE_BODY_COEF

class GameDataSource {

    private var fieldHeight = 0
    private var fieldWidth = 0

    private var snakeX = 0
    private var snakeY = 0

    private var snakeBodySize = 0

    fun setFieldSize(width: Int, height: Int) {
        fieldWidth = width
        fieldHeight = height
        snakeBodySize = fieldHeight / SNAKE_BODY_COEF
    }

    fun getFieldHeight(): Int = fieldHeight

    fun getFieldWidth(): Int = fieldWidth

    fun setSnakePosition(x: Int = snakeX, y: Int = snakeY) {
        snakeX = x
        snakeY = y
    }

    fun getSnakeX(): Int = snakeX

    fun getSnakeY(): Int = snakeY
}