package com.yakushev.spring.feature.game.domain.model

sealed interface GameState {
    object Pause : GameState
    object Play : GameState
    data class Potracheno(val length: Int) : GameState
}
