package com.yakushev.spring.presentation

import android.app.Application
import android.content.Context
import com.yakushev.spring.di.AppComponent
import com.yakushev.spring.di.DaggerAppComponent

class SpringApp : Application() {
    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
    }

    fun getAppComponent(): AppComponent = appComponent
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is SpringApp -> this.getAppComponent()
        else -> applicationContext.appComponent
    }