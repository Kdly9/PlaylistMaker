package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.data.db.FavouritesTrackRepositoryImpl
import com.example.playlistmaker.media.data.db.PlaylistRepositoryImpl
import com.example.playlistmaker.media.domain.api.FavouritesTrackRepository
import com.example.playlistmaker.media.domain.api.PlaylistRepository
import org.koin.dsl.module

val mediaRepositoryModule = module {
    single<FavouritesTrackRepository> {
        FavouritesTrackRepositoryImpl(get(), get())
    }
    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get())
    }
}