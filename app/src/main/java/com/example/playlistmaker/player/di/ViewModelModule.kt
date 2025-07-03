package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val playerViewModelModule = module {
    viewModelOf(::PlayerViewModel)
}