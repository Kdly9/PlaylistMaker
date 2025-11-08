package com.example.playlistmaker.settings.ui

import android.app.Application
import com.example.playlistmaker.media.di.mediaLibraryViewModelModule
import com.example.playlistmaker.player.di.playerDataModule
import com.example.playlistmaker.player.di.playerInteractorModule
import com.example.playlistmaker.player.di.playerViewModelModule
import com.example.playlistmaker.search.di.interactorModule
import com.example.playlistmaker.search.di.searchDataModule
import com.example.playlistmaker.search.di.searchRepositoryModule
import com.example.playlistmaker.search.di.searchViewModelModule
import com.example.playlistmaker.settings.di.settingsDataModule
import com.example.playlistmaker.settings.di.settingsInteractorModule
import com.example.playlistmaker.settings.di.settingsRepositoryModule
import com.example.playlistmaker.settings.di.settingsViewModelModule
import com.example.playlistmaker.sharing.di.sharingDataModule
import com.example.playlistmaker.sharing.di.sharingInteractorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                searchDataModule,
                interactorModule,
                searchRepositoryModule,
                searchViewModelModule,
                settingsInteractorModule,
                settingsRepositoryModule,
                settingsDataModule,
                settingsViewModelModule,
                sharingInteractorModule,
                sharingDataModule,
                playerInteractorModule,
                playerDataModule,
                playerViewModelModule,
                mediaLibraryViewModelModule
            )
        }
    }
}