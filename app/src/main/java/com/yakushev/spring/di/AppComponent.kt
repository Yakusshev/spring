package com.yakushev.spring.di

import com.yakushev.spring.core.MainActivity
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}