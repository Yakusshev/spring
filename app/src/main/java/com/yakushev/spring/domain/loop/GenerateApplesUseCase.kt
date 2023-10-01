package com.yakushev.spring.domain.loop

import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.domain.model.ApplePointModel
import javax.inject.Inject

class GenerateApplesUseCase @Inject constructor(
    private val dataSource: GameDataSource
) {
    suspend operator fun invoke() {
        val height = dataSource.getFieldHeight()
        val width = dataSource.getFieldWidth()
        dataSource.updateAppleListState { list ->
            if (list.isNotEmpty()) return@updateAppleListState list
            val newList = mutableListOf<ApplePointModel>()
            repeat(3) {
                newList.add(
                    ApplePointModel(
                        x = (Math.random() * width).toInt(),
                        y = (Math.random() * height).toInt()
                    )
                )
            }
            newList
        }
    }
}