package com.yakushev.spring.app.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yakushev.spring.core.data.PrivateDataSource
import com.yakushev.spring.core.di.DaggerViewModelFactory
import com.yakushev.spring.core.di.ViewModelKey
import com.yakushev.spring.feature.game.data.GameDataSource
import com.yakushev.spring.feature.game.presentation.GameViewModel
import com.yakushev.spring.feature.test.presentation.TestViewModel
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
    @ViewModelKey(TestViewModel::class)
    fun bindsMainMenuViewModel(viewModel: TestViewModel): ViewModel

    companion object {

        private const val SHARED_PREFERENCES = "SHARED_PREFERENCES"

        @Provides
        @Singleton
        fun provideGameDataSource(): GameDataSource = GameDataSource()

        @Provides
        @Singleton
        fun provideTestDataSource(context: Context): PrivateDataSource =
            PrivateDataSource(context.getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE))
    }
}