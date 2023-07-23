package com.yakushev.spring.presentation.mainmenu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yakushev.spring.core.appComponent
import dagger.Component.Factory
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

//todo delete
class MainMenuFragment : Fragment() {

    private val viewModel: MainMenuViewModel by viewModels { factory.create() }

    @Inject
    lateinit var factory: MainMenuViewModelFactory.Factory

    override fun onAttach(context: Context) {
        context.appComponent.inject(this)
        super.onAttach(context)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}

class MainMenuViewModelFactory @AssistedInject constructor() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == MainMenuViewModel::class)
        return MainMenuViewModel() as T
    }

    @AssistedFactory
    interface Factory {
        fun create(): MainMenuViewModelFactory
    }
}