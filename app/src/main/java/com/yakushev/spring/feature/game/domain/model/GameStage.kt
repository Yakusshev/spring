package com.yakushev.spring.feature.game.domain.model

sealed interface GameStage {
    object Pause : GameStage
    object Play : GameStage
    data class Potracheno(val length: Int) : GameStage
}
