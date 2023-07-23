package com.yakushev.spring.di

import com.yakushev.spring.core.MainActivity
import com.yakushev.spring.presentation.mainmenu.MainMenuFragment
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: MainMenuFragment)
}