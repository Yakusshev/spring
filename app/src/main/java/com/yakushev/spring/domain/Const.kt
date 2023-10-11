package com.yakushev.spring.domain

import com.yakushev.spring.domain.model.DirectionEnum

object Const {
    const val DELAY = 1000f / 125f

    const val SNAKE_BODY_COEF = 0.025f
    const val SNAKE_SPEED = 7f
    const val GROW = 3 * SNAKE_SPEED

    const val SNAKE_LENGTH = 0.075f

    const val APPLE_DELAY = 5 * 1000L

    val DEFAULT_DIRECTION = DirectionEnum.UP
}