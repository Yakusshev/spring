package com.yakushev.spring.core.data

import android.content.SharedPreferences

class PrivateDataSource(
    private val sp: SharedPreferences
) {

    fun putFloat(key: String, value: Float) {
        sp.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String, defaultValue: Float): Float =
        sp.getFloat(key, defaultValue)

}