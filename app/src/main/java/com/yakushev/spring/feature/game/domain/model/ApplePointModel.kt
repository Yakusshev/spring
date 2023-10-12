package com.yakushev.spring.feature.game.domain.model

data class ApplePointModel(
    val x: Float,
    val y: Float,
) {
    companion object {
        val empty = ApplePointModel(
            x = 0f,
            y = 0f,
        )
    }
}