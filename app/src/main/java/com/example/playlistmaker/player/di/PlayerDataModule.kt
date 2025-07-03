package com.example.playlistmaker.player.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.MediaManagerImpl
import com.example.playlistmaker.player.domain.api.MediaManager
import org.koin.dsl.module

val playerDataModule = module {

    single { MediaPlayer() }

    single<MediaManager> { MediaManagerImpl(get()) }

}