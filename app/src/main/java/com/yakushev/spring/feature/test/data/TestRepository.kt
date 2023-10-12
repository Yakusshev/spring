package com.yakushev.spring.feature.test.data

import com.yakushev.spring.core.Const
import com.yakushev.spring.core.data.PrivateDataSource
import javax.inject.Inject

class TestRepository @Inject constructor(private val dataSource: PrivateDataSource) {

    fun setDelay(fps: Float) {
        dataSource.putFloat(FPS, fps)
    }

    fun getDelay(): Float =
        dataSource.getFloat(FPS, Const.DELAY)

    fun setSnakeBodyCoef(coef: Float) {
        dataSource.putFloat(SNAKE_BODY_COEF, coef)
    }

    fun getSnakeBodyCoef(): Float =
        dataSource.getFloat(SNAKE_BODY_COEF, Const.SNAKE_BODY_COEF)

    companion object {
        private const val FPS = "FPS_LOCK"
        private const val SNAKE_BODY_COEF = "SNAKE_BODY_COEF"
    }
}