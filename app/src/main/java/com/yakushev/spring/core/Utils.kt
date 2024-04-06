package com.yakushev.spring.core

import android.util.Log
import com.yakushev.spring.feature.game.domain.model.SnakePointModel

fun <T> T.log(message: String): T {
    Log.d("###", "$message: $this")
    return this
}

fun List<SnakePointModel>.toText(): String {
    var string = ""
    forEachIndexed { index, snakePointModel ->
        string += "$index. $snakePointModel| "
    }
    return "$string."
}