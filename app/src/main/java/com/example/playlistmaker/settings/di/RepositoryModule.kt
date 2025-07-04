package com.example.playlistmaker.settings.di

import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import org.koin.dsl.module

val settingsRepositoryModule = module {

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }
}