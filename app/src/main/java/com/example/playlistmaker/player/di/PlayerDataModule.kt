package com.example.playlistmaker.player.di

import com.example.playlistmaker.player.data.MediaManagerImpl
import com.example.playlistmaker.player.domain.api.MediaManager
import org.koin.dsl.module

val playerDataModule = module {

    single<MediaManager> {
        MediaManagerImpl()
    }

}