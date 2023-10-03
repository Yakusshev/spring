package com.yakushev.spring.di

import com.yakushev.spring.presentation.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}