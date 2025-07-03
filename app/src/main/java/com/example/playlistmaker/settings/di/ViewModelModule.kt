package com.example.playlistmaker.settings.di

import android.app.Activity
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {

    viewModel {
        (activity: Activity)->
        SettingsViewModel(sharingInteractor = get(), themeInteractor = get())
    }
}