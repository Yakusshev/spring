package com.yakushev.spring.app

import android.app.Application
import android.content.Context
import com.yakushev.spring.app.di.AppComponent
import com.yakushev.spring.app.di.DaggerAppComponent

class SpringApp : Application() {
    private val component: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .context(applicationContext)
            .build()
    }

    fun getAppComponent(): AppComponent = component
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is SpringApp -> getAppComponent()
        else -> applicationContext.appComponent
    }