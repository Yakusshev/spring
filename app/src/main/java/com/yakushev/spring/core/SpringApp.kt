package com.yakushev.spring.core

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
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is SpringApp -> appComponent
        else -> applicationContext.appComponent
    }