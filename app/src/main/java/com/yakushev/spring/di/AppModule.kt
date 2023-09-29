package com.yakushev.spring.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yakushev.spring.core.di.DaggerViewModelFactory
import com.yakushev.spring.core.di.ViewModelKey
import com.yakushev.spring.data.GameDataSource
import com.yakushev.spring.presentation.game.GameViewModel
import com.yakushev.spring.presentation.mainmenu.MainMenuViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
interface AppModule {
    @Binds
    fun bindsViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(GameViewModel::class)
    fun bindsGameViewModel(viewModel: GameViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainMenuViewModel::class)
    fun bindsMainMenuViewModel(viewModel: MainMenuViewModel): ViewModel

    companion object {

        @Provides
        @Singleton
        fun provideGameDataSource(): GameDataSource = GameDataSource()
    }
}