package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.domain.api.FavoritesTracksInteractor
import com.example.playlistmaker.media.domain.api.PlaylistInteractor
import com.example.playlistmaker.media.domain.impl.FavoritesTracksInteractorImpl
import com.example.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import org.koin.dsl.module

val favororiteInteractorModule = module {
    factory<FavoritesTracksInteractor> {
        FavoritesTracksInteractorImpl(get())
    }

    factory<PlaylistInteractor> { PlaylistInteractorImpl(get()) }
}