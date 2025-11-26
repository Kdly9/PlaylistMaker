package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.media.domain.impl.FavoritesTracksInteractorImpl
import org.koin.dsl.module

val favororiteInteractorModule = module {
    single<FavoritesTracksInteractor> {
        FavoritesTracksInteractorImpl(get())
    }
}