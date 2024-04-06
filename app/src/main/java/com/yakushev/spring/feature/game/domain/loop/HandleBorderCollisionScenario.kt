package com.yakushev.spring.feature.game.domain.loop

import com.yakushev.spring.feature.game.data.GameDataSource
import javax.inject.Inject

internal class HandleBorderCollisionScenario @Inject constructor(
    private val dataSource: GameDataSource,
) {
    suspend operator fun invoke() {

    }
}
