package com.yakushev.spring.feature.game.domain.loop

import com.yakushev.spring.core.Const
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.domain.model.ApplePointModel
import javax.inject.Inject

internal class GenerateApplesUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    operator fun invoke(reset: Boolean = false) {
        val height = dataSource.getFieldHeight().toInt()
        val width = dataSource.getFieldWidth().toInt()
        dataSource.updateAppleListState { list ->
            if (list.isNotEmpty() && !reset) return@updateAppleListState list
            val newList = mutableListOf<ApplePointModel>()
            val safe = (width * Const.SPAWN_SAFE_ZONE).toInt()
            repeat(3) {
                newList.add(
                    ApplePointModel(
                        x = (safe..width - safe).random().toFloat(),
                        y = (safe..height - safe).random().toFloat()
                    )
                )
            }
            newList
        }
    }
}