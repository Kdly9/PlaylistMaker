package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.domain.api.MediaInteractor
import com.example.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import org.koin.dsl.module

val playerInteractorModule = module {

    single<MediaInteractor> {
        MediaPlayerInteractorImpl(get())
    }

}