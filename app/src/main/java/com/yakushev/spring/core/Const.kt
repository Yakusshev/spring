package com.yakushev.spring.core

import com.yakushev.spring.feature.game.domain.model.DirectionEnum

object Const {
    const val APPLE_COLLISION_COEF = .66
    const val DELAY = 1000f / 125f

    const val SNAKE_BODY_COEF = 0.025f
    const val SNAKE_SPEED = 10f
    const val GROW = 3 * SNAKE_SPEED
    const val SNAKE_LENGTH = 0.075f

    const val SPAWN_SAFE_ZONE = 0.05f


    val DEFAULT_DIRECTION = DirectionEnum.UP
}