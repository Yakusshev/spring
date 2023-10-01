package com.yakushev.spring.domain.model

data class ApplePointModel(
    val x: Int,
    val y: Int,
) {
    companion object {
        val empty = ApplePointModel(
            x = 0,
            y = 0,
        )
    }
}