package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import org.koin.dsl.module

val settingsInteractorModule = module {

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }
}